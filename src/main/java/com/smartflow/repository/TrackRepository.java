package com.smartflow.repository;

import com.smartflow.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с треками
 * 
 * Предоставляет методы для поиска, фильтрации и управления треками.
 * Оптимизирован для быстрого поиска по названию, исполнителю и жанрам.
 */
@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    /**
     * Поиск треков по названию (без учета регистра)
     * 
     * @param title название трека
     * @param pageable параметры пагинации
     * @return страница с найденными треками
     */
    Page<Track> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Поиск треков по исполнителю (без учета регистра)
     * 
     * @param artist имя исполнителя
     * @param pageable параметры пагинации
     * @return страница с найденными треками
     */
    Page<Track> findByArtistContainingIgnoreCase(String artist, Pageable pageable);

    /**
     * Полнотекстовый поиск по названию и исполнителю
     * 
     * @param query поисковый запрос
     * @param pageable параметры пагинации
     * @return страница с найденными треками
     */
    @Query("SELECT t FROM Track t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.artist) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(t.album) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Track> searchTracks(@Param("query") String query, Pageable pageable);

    /**
     * Поиск самых популярных треков по количеству прослушиваний
     * 
     * @param pageable параметры пагинации
     * @return страница с популярными треками
     */
    Page<Track> findByIsAvailableTrueOrderByPlayCountDesc(Pageable pageable);

    /**
     * Поиск треков по жанру
     * 
     * @param genreId идентификатор жанра
     * @param pageable параметры пагинации
     * @return страница с треками указанного жанра
     */
    @Query("SELECT t FROM Track t JOIN t.genres g WHERE g.id = :genreId AND t.isAvailable = true")
    Page<Track> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);
}

