package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Добавляем фильм " + film);
        inMemoryFilmStorage.addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновляем фильм " + film);
        inMemoryFilmStorage.updateFilm(film);
        return film;
    }

    @GetMapping("/{id}")
    public Film getUserById(@PathVariable Long id) {
        log.info("Получаем фильм по id: " + id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable Long id, @PathVariable @Min(1) Long userId) {
        log.info("Пользователь по id: " + userId + " ставит лайк фильму по id: " + id);
        filmService.addLikeFilm(id, userId);
    }

    @GetMapping
    public List<Film> listFilms() {
        log.info("Получаем список фильмов, их количество: " + inMemoryFilmStorage.listFilms().size());
        return inMemoryFilmStorage.listFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "0") Integer count) {
        log.info("Показываем по популярности фильмы");
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable String id, @PathVariable @Min(1) String userId) {
        log.info("Пользователь по id: " + userId + " удаляет лайк фильму по id: " + id);
        filmService.deleteLikeFilm(id, userId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationException handleException(ConstraintViolationException exception) {
        return new ValidationException(exception.getMessage());
    }

}
