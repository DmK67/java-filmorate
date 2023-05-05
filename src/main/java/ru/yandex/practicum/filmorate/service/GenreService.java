package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class GenreService {

    private final GenreDao genreDao;

    public List<Genre> listGenres() {
        return genreDao.listGenres();
    }

    public Genre getGenreById(Long id) {
        return genreDao.getGenreById(Math.toIntExact(id));
    }
}
