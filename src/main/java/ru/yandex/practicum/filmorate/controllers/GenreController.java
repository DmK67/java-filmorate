package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping({"/genres"})
@Slf4j
@Validated
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping()
    public List<Genre> listGenres() {
        log.info("Получаем список жанров, их количество: " + genreService.listGenres().size());
        return genreService.listGenres();
    }

    @GetMapping("/{id}")
    public Genre getMpaById(@PathVariable Long id) {
        log.info("Получаем жанр по id: " + id);
        return genreService.getGenreById(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationException handleException(ConstraintViolationException exception) {
        return new ValidationException(exception.getMessage());
    }

}
