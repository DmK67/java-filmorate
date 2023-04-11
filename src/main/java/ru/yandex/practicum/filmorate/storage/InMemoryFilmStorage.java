package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;

    private boolean resultValidFilm;

    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film addFilm(Film film) {
        resultValidFilm = validationFilm(film);
        if (resultValidFilm) {
            film.setId(++id);
            films.put(film.getId(), film);
            log.info(film + " Фильм успешно добавлен!");
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Ошибка! Такой фильм не найден...!");
            throw new NotFoundException("Ошибка! Такой фильм не найден...");
        }
        resultValidFilm = validationFilm(film);
        if (resultValidFilm) {
            films.put(film.getId(), film);
            log.info(film + " Фильм успешно обновлен");
        }
        return film;
    }

    @Override
    public List<Film> listFilms() {
        return new ArrayList<>(films.values());
    }

    public boolean validationFilm(Film film) {
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
        return true;
    }

}
