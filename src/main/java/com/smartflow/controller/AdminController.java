package com.smartflow.controller;

import com.smartflow.dto.TrackDTO;
import com.smartflow.dto.WaveResponseDTO;
import com.smartflow.model.Genre;
import com.smartflow.model.Track;
import com.smartflow.model.User;
import com.smartflow.repository.GenreRepository;
import com.smartflow.repository.TrackRepository;
import com.smartflow.repository.UserRepository;
import com.smartflow.service.ConfigService;
import com.smartflow.service.RecommendationService;
import com.smartflow.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для админ-панели
 * 
 * Предоставляет веб-интерфейс для управления:
 * - Конфигурацией рекомендаций
 * - Пользователями
 * - Треками
 * - Жанрами
 * - Мониторингом системы
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ConfigService configService;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final RecommendationService recommendationService;
    private final SearchService searchService;

    /**
     * Главная страница админ-панели (Dashboard)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Статистика
        long totalUsers = userRepository.count();
        long totalTracks = trackRepository.count();
        long totalGenres = genreRepository.count();
        long activeUsers = userRepository.findAll().stream()
                .filter(User::getIsActive)
                .count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("totalTracks", totalTracks);
        model.addAttribute("totalGenres", totalGenres);
        model.addAttribute("config", configService.getConfig());

        return "admin/dashboard";
    }

    /**
     * Страница входа
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        return "admin/login";
    }

    /**
     * Страница управления конфигурацией рекомендаций
     */
    @GetMapping("/config")
    public String config(Model model) {
        model.addAttribute("config", configService.getConfig());
        return "admin/config";
    }

    /**
     * API: Обновление конфигурации
     */
    @PostMapping("/config/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody Map<String, Object> newConfig) {
        try {
            configService.updateConfig(newConfig);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Конфигурация успешно обновлена");
            response.put("config", configService.getConfig());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при обновлении конфигурации: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Страница управления пользователями
     */
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        
        return "admin/users";
    }

    /**
     * Страница редактирования пользователя
     */
    @GetMapping("/users/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<Genre> allGenres = genreRepository.findAll();
        
        model.addAttribute("user", user);
        model.addAttribute("allGenres", allGenres);
        return "admin/user-edit";
    }

    /**
     * API: Обновление пользователя
     */
    @PostMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> updates) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            
            if (updates.containsKey("displayName")) {
                user.setDisplayName((String) updates.get("displayName"));
            }
            if (updates.containsKey("isActive")) {
                user.setIsActive((Boolean) updates.get("isActive"));
            }
            
            userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Пользователь обновлен");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Страница управления треками
     */
    @GetMapping("/tracks")
    public String tracks(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size,
                         @RequestParam(required = false) String search,
                         Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> trackPage;
        
        if (search != null && !search.trim().isEmpty()) {
            trackPage = trackRepository.searchTracks(search.trim(), pageable);
        } else {
            trackPage = trackRepository.findAll(pageable);
        }
        
        model.addAttribute("tracks", trackPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", trackPage.getTotalPages());
        model.addAttribute("totalElements", trackPage.getTotalElements());
        model.addAttribute("search", search);
        
        return "admin/tracks";
    }

    /**
     * Страница редактирования трека
     */
    @GetMapping("/tracks/{id}")
    public String editTrack(@PathVariable Long id, Model model) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));
        List<Genre> allGenres = genreRepository.findAll();
        
        model.addAttribute("track", track);
        model.addAttribute("allGenres", allGenres);
        return "admin/track-edit";
    }

    /**
     * API: Обновление трека
     */
    @PostMapping("/tracks/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTrack(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> updates) {
        try {
            Track track = trackRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Трек не найден"));
            
            if (updates.containsKey("title")) {
                track.setTitle((String) updates.get("title"));
            }
            if (updates.containsKey("artist")) {
                track.setArtist((String) updates.get("artist"));
            }
            if (updates.containsKey("album")) {
                track.setAlbum((String) updates.get("album"));
            }
            if (updates.containsKey("isAvailable")) {
                track.setIsAvailable((Boolean) updates.get("isAvailable"));
            }
            
            trackRepository.save(track);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Трек обновлен");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при обновлении трека: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Страница управления жанрами
     */
    @GetMapping("/genres")
    public String genres(Model model) {
        List<Genre> allGenres = genreRepository.findAll();
        model.addAttribute("genres", allGenres);
        return "admin/genres";
    }

    /**
     * API: Создание жанра
     */
    @PostMapping("/genres")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createGenre(@RequestBody Map<String, String> data) {
        try {
            Genre genre = Genre.builder()
                    .name(data.get("name"))
                    .description(data.get("description"))
                    .build();
            
            genreRepository.save(genre);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Жанр создан");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при создании жанра: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Ошибка: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Страница мониторинга
     */
    @GetMapping("/monitoring")
    public String monitoring(Model model) {
        // Получаем статистику рекомендаций
        try {
            WaveResponseDTO recommendations = recommendationService.getRecommendations(1L, 10);
            model.addAttribute("recommendationsCount", recommendations.getTotalRecommendations());
        } catch (Exception e) {
            log.warn("Не удалось получить статистику рекомендаций: {}", e.getMessage());
        }
        
        model.addAttribute("config", configService.getConfig());
        return "admin/monitoring";
    }
}
