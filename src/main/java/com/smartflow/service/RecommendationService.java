package com.smartflow.service;

import com.smartflow.dto.TrackDTO;
import com.smartflow.dto.WaveResponseDTO;
import com.smartflow.model.Genre;
import com.smartflow.model.Track;
import com.smartflow.model.User;
import com.smartflow.repository.GenreRepository;
import com.smartflow.repository.TrackRepository;
import com.smartflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для генерации персонализированных рекомендаций
 * 
 * Анализирует предпочтения пользователя и историю прослушиваний
 * для создания персонализированного плейлиста рекомендаций.
 * Использует гибридный подход: на основе жанров и популярности треков.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;

    /**
     * Генерация рекомендаций для пользователя (Wave API)
     * 
     * Алгоритм рекомендаций:
     * 1. Анализирует предпочтения пользователя по жанрам
     * 2. Ищет популярные треки в предпочитаемых жанрах
     * 3. Исключает уже добавленные в избранное треки
     * 4. Сортирует по популярности и релевантности
     * 
     * @param userId идентификатор пользователя
     * @param limit количество рекомендаций (по умолчанию 20)
     * @return список рекомендованных треков
     */
    @Cacheable(value = "recommendations", key = "#userId + '_' + #limit")
    public WaveResponseDTO getRecommendations(Long userId, int limit) {
        log.debug("Генерация рекомендаций для пользователя ID: {}, лимит: {}", userId, limit);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Пользователь с ID {} не найден", userId);
            return getDefaultRecommendations(limit);
        }

        User user = userOpt.get();
        Set<Long> favoriteTrackIds = user.getFavoriteTracks().stream()
                .map(Track::getId)
                .collect(Collectors.toSet());

        // Получаем предпочитаемые жанры пользователя
        Set<Genre> preferredGenres = user.getPreferredGenres();
        
        List<Track> recommendations = new ArrayList<>();

        if (!preferredGenres.isEmpty()) {
            // Рекомендации на основе предпочитаемых жанров
            recommendations = getRecommendationsByGenres(preferredGenres, favoriteTrackIds, limit);
            log.info("Найдено {} рекомендаций на основе жанров для пользователя {}", 
                    recommendations.size(), userId);
        }

        // Если рекомендаций недостаточно, добавляем популярные треки
        if (recommendations.size() < limit) {
            int remaining = limit - recommendations.size();
            List<Track> popularTracks = getPopularTracks(favoriteTrackIds, remaining);
            recommendations.addAll(popularTracks);
            log.info("Добавлено {} популярных треков для пользователя {}", 
                    popularTracks.size(), userId);
        }

        // Преобразуем в DTO
        List<TrackDTO> trackDTOs = recommendations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return WaveResponseDTO.builder()
                .recommendations(trackDTOs)
                .totalRecommendations(trackDTOs.size())
                .source(preferredGenres.isEmpty() ? "popular" : "genres")
                .build();
    }

    /**
     * Получение рекомендаций на основе жанров
     * 
     * @param genres предпочитаемые жанры
     * @param excludeTrackIds идентификаторы треков для исключения
     * @param limit лимит результатов
     * @return список рекомендованных треков
     */
    private List<Track> getRecommendationsByGenres(Set<Genre> genres, Set<Long> excludeTrackIds, int limit) {
        List<Track> allRecommendations = new ArrayList<>();

        for (Genre genre : genres) {
            Pageable pageable = PageRequest.of(0, limit, Sort.by("playCount").descending());
            var tracks = trackRepository.findByGenreId(genre.getId(), pageable).getContent();
            
            // Фильтруем исключенные треки
            tracks = tracks.stream()
                    .filter(track -> !excludeTrackIds.contains(track.getId()))
                    .filter(track -> track.getIsAvailable())
                    .collect(Collectors.toList());
            
            allRecommendations.addAll(tracks);
        }

        // Удаляем дубликаты и сортируем по популярности
        return allRecommendations.stream()
                .distinct()
                .sorted(Comparator.comparing(Track::getPlayCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Получение популярных треков
     * 
     * @param excludeTrackIds идентификаторы треков для исключения
     * @param limit лимит результатов
     * @return список популярных треков
     */
    private List<Track> getPopularTracks(Set<Long> excludeTrackIds, int limit) {
        Pageable pageable = PageRequest.of(0, limit * 2, Sort.by("playCount").descending());
        return trackRepository.findByIsAvailableTrueOrderByPlayCountDesc(pageable)
                .getContent()
                .stream()
                .filter(track -> !excludeTrackIds.contains(track.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Получение рекомендаций по умолчанию (для новых пользователей)
     * 
     * @param limit лимит результатов
     * @return список популярных треков
     */
    private WaveResponseDTO getDefaultRecommendations(int limit) {
        log.info("Генерация рекомендаций по умолчанию, лимит: {}", limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by("playCount").descending());
        List<Track> popularTracks = trackRepository
                .findByIsAvailableTrueOrderByPlayCountDesc(pageable)
                .getContent();

        List<TrackDTO> trackDTOs = popularTracks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return WaveResponseDTO.builder()
                .recommendations(trackDTOs)
                .totalRecommendations(trackDTOs.size())
                .source("popular")
                .build();
    }

    /**
     * Преобразование сущности Track в DTO
     * 
     * @param track сущность трека
     * @return DTO трека
     */
    private TrackDTO convertToDTO(Track track) {
        return TrackDTO.builder()
                .id(track.getId())
                .title(track.getTitle())
                .artist(track.getArtist())
                .album(track.getAlbum())
                .duration(track.getDuration())
                .audioUrl(track.getAudioUrl())
                .coverUrl(track.getCoverUrl())
                .genres(track.getGenres().stream()
                        .map(Genre::getName)
                        .collect(Collectors.toSet()))
                .playCount(track.getPlayCount())
                .build();
    }
}

