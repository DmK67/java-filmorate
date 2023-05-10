package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> listFilms();

    Film getFilmById(Long id);

    void addLikeFilmToUser(Long id, Long userId);

    void deleteLikeFilmToUser(Long id, Long userId);

}
