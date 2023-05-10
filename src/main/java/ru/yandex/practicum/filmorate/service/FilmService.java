package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;

    public void addLikeFilm(Long id, Long userId) { //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
        userDao.getUserById(userId);
        Film film = filmDao.getFilmById(id);
        filmDao.addLikeFilmToUser(id, userId);
        film.setLikes(userId);
        log.info("Пользователь по id: " + userId + " поставил Like фильму " + film);
    }

    public void deleteLikeFilm(Long id, Long userId) { //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
        userDao.getUserById(userId);
        Film film = filmDao.getFilmById(id);
        film.getLikes().remove(userId);
        filmDao.deleteLikeFilmToUser(id, userId);
        log.info("Пользователь по id: " + userId + " удалил Like фильму " + film);
    }


    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.
    public List<Film> getPopularFilms(Integer count) {
        List<Film> sortedByLikesFilms = filmDao.listFilms();
        sortedByLikesFilms.sort(Comparator.comparingLong(o -> o.getLikes().size()));
        Collections.reverse(sortedByLikesFilms);
        if (count != 0 && sortedByLikesFilms.size() >= count) {
            return sortedByLikesFilms.subList(0, count);
        } else {
            if (sortedByLikesFilms.size() >= 11) {
                return sortedByLikesFilms.subList(0, 9);
            } else {
                return sortedByLikesFilms;
            }
        }
    }

    public Film getFilmById(Long id) {
        return filmDao.getFilmById(id);
    }

    public Film addFilm(Film film) {
        validationFilm(film);
        return filmDao.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validationFilm(film);
        return filmDao.updateFilm(film);
    }

    public List<Film> listFilms() {
        return filmDao.listFilms();
    }

    private Film validationFilm(Film film) {
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
        if (film.getGenres() == null || film.getLikes() == null) {
            return Film.builder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .mpa(film.getMpa())
                    .genres(new ArrayList<>())
                    .build();
        } else {
            return film;
        }
    }

}