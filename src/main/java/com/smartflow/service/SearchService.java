package com.smartflow.service;

import com.smartflow.dto.SearchResponseDTO;
import com.smartflow.dto.TrackDTO;
import com.smartflow.model.Genre;
import com.smartflow.model.Track;
import com.smartflow.repository.TrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Сервис для поиска треков
 * 
 * Реализует полнотекстовый поиск по трекам с поддержкой пагинации.
 * Использует кэширование для оптимизации производительности при частых запросах.
 */
@Service
@Transactional(readOnly = true)
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final TrackRepository trackRepository;

    public SearchService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    /**
     * Поиск треков по запросу
     * 
     * Выполняет полнотекстовый поиск по названию, исполнителю и альбому.
     * Результаты кэшируются для повышения производительности.
     * 
     * @param query поисковый запрос
     * @param page номер страницы (начиная с 0)
     * @param size размер страницы
     * @return результаты поиска с пагинацией
     */
    @Cacheable(value = "searchResults", key = "#query + '_' + #page + '_' + #size")
    public SearchResponseDTO searchTracks(String query, int page, int size) {
        log.debug("Выполняется поиск треков по запросу: '{}', страница: {}, размер: {}", query, page, size);

        // Создаем параметры пагинации с сортировкой по популярности
        Pageable pageable = PageRequest.of(page, size, Sort.by("playCount").descending());

        // Выполняем поиск
        Page<Track> trackPage = trackRepository.searchTracks(query, pageable);

        // Преобразуем результаты в DTO
        var tracks = trackPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Найдено {} треков по запросу '{}'", trackPage.getTotalElements(), query);

        return new SearchResponseDTO(
                tracks,
                trackPage.getTotalElements(),
                trackPage.getNumber(),
                trackPage.getTotalPages(),
                trackPage.getSize()
        );
    }

    /**
     * Поиск треков по жанру
     * 
     * @param genreId идентификатор жанра
     * @param page номер страницы
     * @param size размер страницы
     * @return результаты поиска
     */
    @Cacheable(value = "genreTracks", key = "#genreId + '_' + #page + '_' + #size")
    public SearchResponseDTO searchTracksByGenre(Long genreId, int page, int size) {
        log.debug("Поиск треков по жанру ID: {}, страница: {}, размер: {}", genreId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("playCount").descending());
        Page<Track> trackPage = trackRepository.findByGenreId(genreId, pageable);

        var tracks = trackPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new SearchResponseDTO(
                tracks,
                trackPage.getTotalElements(),
                trackPage.getNumber(),
                trackPage.getTotalPages(),
                trackPage.getSize()
        );
    }

    /**
     * Преобразование сущности Track в DTO
     * 
     * @param track сущность трека
     * @return DTO трека
     */
    private TrackDTO convertToDTO(Track track) {
        return new TrackDTO(
                track.getId(),
                track.getTitle(),
                track.getArtist(),
                track.getAlbum(),
                track.getDuration(),
                track.getAudioUrl(),
                track.getCoverUrl(),
                track.getGenres().stream()
                        .map(Genre::getName)
                        .collect(Collectors.toSet()),
                track.getPlayCount()
        );
    }
}

