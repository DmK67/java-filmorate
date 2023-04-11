package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    void start() {
        film = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2018, 12, 17))
                .duration(60).build();
        //filmController = new FilmController(inMemoryFilmStorage);
    }

    @Test
    void addFilmValidationDescription() {
        film.setDescription("qwertyuiopasdfghjklmnbvcxzqwerqwertyuiopasdfghjklmnbvcxzqwerqwertyuiopasdfgh" +
                "jklmnbvcxzqwerqwertyuiopasdfghjklmnbcxzqwerqwertyuiopasdfghjklmnbvcxzqwerqwertyuiopasdfg" +
                "hjklmnbvcxzqwerqwertyuiopasdfghjklmnbvcxzqwerqwertyuiopasdfghjklmnbvcxzqwerqwertyuiopasd");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertEquals("Максимальная длина описания фильма — 200 символов.", exception.getMessage());
    }

    @Test
    void addFilmValidationReleaseDate() {
        film.setReleaseDate(LocalDate.of(1750, 12, 17));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertEquals("Дата релиза — не может быть раньше 28.12.1895 г.", exception.getMessage());
    }

    @Test
    void addFilmValidationDuration() {
        film.setDuration(0);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });

        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
    }

    @Test
    void getlistFilms() {
        filmController.addFilm(film);

        assertEquals(1, filmController.listFilms().size(), "Количество фильмов не соответствует!");
        assertNotNull(filmController.listFilms(), "Список фильмов пустой!");
    }
}