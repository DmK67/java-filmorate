package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;

    public void addLikeFilm(@Valid Long id, Long userId) { //PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
        Film film = inMemoryFilmStorage.getFilms().get(id);
        film.setLikes(userId);
        log.info("Пользователь по id: " + userId + " поставил Like фильму " + film);
    }

    public void deleteLikeFilm(@Valid String id, String userId) { //DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
        id = (id.replace("{", "")).replace("}", "");
        userId = (userId.replace("{", "")).replace("}", "");
        Film film = inMemoryFilmStorage.getFilms().get(Long.parseLong(id));
        film.getLikes().remove(Long.parseLong(userId));
        log.info("Пользователь по id: " + id + " удалил Like фильму " + film);
    }

    //GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, верните первые 10.
    public List<Film> getPopularFilms(@Valid Integer count) {
        List<Film> sortedByLikesFilms = inMemoryFilmStorage.listFilms();
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

    public Film getFilmById(@Valid Long id) {
        if (!inMemoryFilmStorage.getFilms().containsKey(id)) {
            throw new NotFoundException("Фильм с id: " + id + " не найден");
        }
        return inMemoryFilmStorage.getFilms().get(id);
    }
}
