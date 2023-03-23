package ru.yandex.practicum.filmorate.controllers;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    public Map<Integer, Film> getFilms() {
        return films;
    }

    private Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @PostMapping
    public Film addFilm(@NonNull @RequestBody Film film) {
        log.info("Добавляем фильм " + film);
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.info("Ошибка! Название фильма пустое.");
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            log.info("Ошибка! Описание фильма больше 200 символов!");
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка! Дата релиза — не может быть раньше 28.12.1895 г.!");
            throw new ValidationException("Дата релиза — не может быть раньше 28.12.1895 г.");
        }
        if (film.getDuration() <= 0) {
            log.info("Ошибка! Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (films.containsKey(film.getId())) {
            log.info("Ошибка! Такой фильм уже существует!");
            throw new ValidationException("Невозможно добавить, такой фильм уже существует.");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info(film + " Фильм успешно добавлен!");
        return film;
    }

    @PutMapping
    public Film updateFilm(@NonNull @RequestBody Film film) {
        log.info("Обновляем фильм " + film);
        if (film.getName().isBlank()) {
            log.info("Ошибка! Название фильма пустое.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.info("Ошибка! Описание фильма больше 200 символов!");
            throw new ValidationException("Максимальная длина описания фильма — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка! Дата релиза — не может быть раньше 28.12.1895 г.!");
            throw new ValidationException("Дата релиза — не может быть раньше 28.12.1895 г.");
        }
        if (film.getDuration() <= 0) {
            log.info("Ошибка! Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (!films.containsKey(film.getId())) {
            log.info("Ошибка! Такой фильм не найден...!");
            throw new ValidationException("Ошибка! Такой фильм не найден...");
        }
        films.put(film.getId(), film);
        log.info(film + " Фильм успешно обновлен");

        return film;
    }

    @GetMapping
    public List<Film> listFilms() {
        log.info("Получаем список фильмов, их количество: " + films.size());
        return new ArrayList<>(films.values());
    }

}
