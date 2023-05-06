package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @DirtiesContext
    @Test
    void listGenresTest() throws ValidationException, NotFoundException {
        List<Mpa> listMpa = mpaDbStorage.listMpa();
        assertEquals(5, listMpa.size());
    }

    @DirtiesContext
    @Test
    void getGenreByIdTest() throws ValidationException, NotFoundException {
        Mpa mpa = mpaDbStorage.getMpaById(1);
        assertEquals(1, mpa.getId());
        assertEquals("G", mpa.getName());
    }

}