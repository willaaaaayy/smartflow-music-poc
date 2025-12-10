package com.smartflow.dto;

import java.util.Set;

/**
 * DTO для передачи информации о треке через API
 * 
 * Используется для сериализации данных трека без лишней информации
 * и для оптимизации размера ответа API.
 */
public class TrackDTO {

    /**
     * Идентификатор трека
     */
    private Long id;

    /**
     * Название трека
     */
    private String title;

    /**
     * Исполнитель
     */
    private String artist;

    /**
     * Альбом
     */
    private String album;

    /**
     * Длительность в секундах
     */
    private Integer duration;

    /**
     * URL для прослушивания
     */
    private String audioUrl;

    /**
     * URL обложки
     */
    private String coverUrl;

    /**
     * Названия жанров трека
     */
    private Set<String> genres;

    /**
     * Количество прослушиваний
     */
    private Long playCount;

    public TrackDTO() {
    }

    public TrackDTO(Long id, String title, String artist, String album, Integer duration, String audioUrl,
                    String coverUrl, Set<String> genres, Long playCount) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.coverUrl = coverUrl;
        this.genres = genres;
        this.playCount = playCount;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }
}

