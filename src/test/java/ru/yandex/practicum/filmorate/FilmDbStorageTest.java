package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userDbStorage;

    @DirtiesContext
    @Test
    void addFilmTest() throws ValidationException, NotFoundException {
        Film film1 = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        Film film2 = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        film2.setId(1L);
        filmStorage.addFilm(film1);
        Film film3 = filmStorage.getFilmById(1L);
        assertEquals(film2.getId(), film3.getId());
        assertEquals(film2.getName(), film3.getName());
        assertEquals(film2.getDescription(), film3.getDescription());
        assertEquals(film2.getReleaseDate(), film3.getReleaseDate());
        assertEquals(film2.getDuration(), film3.getDuration());
    }

    @DirtiesContext
    @Test
    void updateFilmTest() throws ValidationException, NotFoundException {
        Film film1 = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(film1);
        Film film2 = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        film2.setId(1L);
        filmStorage.updateFilm(film2);
        Film film3 = filmStorage.getFilmById(1L);
        assertEquals(film2.getId(), film3.getId());
        assertEquals(film2.getName(), film3.getName());
        assertEquals(film2.getDescription(), film3.getDescription());
        assertEquals(film2.getReleaseDate(), film3.getReleaseDate());
        assertEquals(film2.getDuration(), film3.getDuration());
    }

    @DirtiesContext
    @Test
    void listFilmsTest() throws ValidationException, NotFoundException {
        List<Film> films = filmStorage.listFilms();
        assertEquals(0,films.size());
        Film film1 = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(film1);
        films = filmStorage.listFilms();
        assertEquals(1,films.size());
    }

    @DirtiesContext
    @Test
    void getFilmByIdTest() {
        Film film1 = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(film1);
        Film film = filmStorage.getFilmById(1L);
        assertEquals(1, film.getId());
        assertEquals(film1.getName(), film.getName());
        assertEquals(film1.getDescription(), film.getDescription());
        assertEquals(film1.getReleaseDate(), film.getReleaseDate());
        assertEquals(film1.getDuration(), film.getDuration());
    }

    @DirtiesContext
    @Test
    void addLikeFilmToUserTest() {
        Film film = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(film);
        //Film film1 = filmStorage.getFilmById(1L);
        User user = User.builder()
                .email("victory@ya.ru")
                .name("Victory")
                .login("Vic")
                .birthday(LocalDate.of(1945,5,9))
                .build();
        userDbStorage.addUser(user);
        //User user1 = userDbStorage.getUserById(1L);
        filmStorage.addLikeFilmToUser(1L, 1L);
    }

    @Test
    void deleteLikeFilmToUser() {
        Film film = Film.builder()
                .name("Pobeda")
                .description("The victory will be ours")
                .releaseDate(LocalDate.of(1945, 05, 9))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.addFilm(film);
        Film film1 = filmStorage.getFilmById(1L);
        User user = User.builder()
                .email("victory@ya.ru")
                .name("Victory")
                .login("Vic")
                .birthday(LocalDate.of(1945,5,9))
                .build();
    }

}