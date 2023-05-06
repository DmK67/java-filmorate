package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @DirtiesContext
    @Test
    void listGenresTest() throws ValidationException, NotFoundException {
        List<Genre> listGenres = genreDbStorage.listGenres();
        assertEquals(6, listGenres.size());
    }

    @DirtiesContext
    @Test
    void getGenreByIdTest() throws ValidationException, NotFoundException {
        Genre genre = genreDbStorage.getGenreById(1);
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @DirtiesContext
    @Test
    void checkReportExitsGenresTest() throws ValidationException, NotFoundException {
        Genre genre = genreDbStorage.getGenreById(2);
        assertEquals(2L, genre.getId());
        assertEquals("Драма", genre.getName());
    }

}