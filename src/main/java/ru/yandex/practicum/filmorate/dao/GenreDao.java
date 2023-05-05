package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    List<Genre> listGenres();

    Genre getGenreById(int id);

    boolean checkReportExitsGenres(Long id);

    List<Genre> getListGenres(Long id);


}
