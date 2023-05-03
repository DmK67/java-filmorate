package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Component
@Repository
@Slf4j
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        Film validFilm = validationFilm(film);
        String sqlQueryInsert = "insert into FILMS(FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA)" +
                " values (?, ?, ?, ?, ?)";
        Long id = writingToTable(validFilm, sqlQueryInsert);
        log.info(validFilm + " Фильм успешно добавлен!");
        return getFilmById(id);
    }

    private Long writingToTable(Film film, String query) {
        if (film.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setString(3, film.getReleaseDate().toString());
                ps.setLong(4, film.getDuration());
                ps.setLong(5, film.getMpa().getId());
                return ps;
            }, keyHolder);
            return Long.parseLong(keyHolder.getKey().toString());
        } else {
            jdbcTemplate.update(query
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getId());
            film.setMpa(getMpaById(film.getMpa().getId()));
            return Long.parseLong(film.getId().toString());
        }
    }

    private Film validationFilm(Film film) {
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

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        Film validFilm = validationFilm(film);
        String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?," +
                " FILM_DURATION = ?, FILM_MPA = ? where FILM_ID = ?";
        writingToTable(validFilm, sqlQuery);
        log.info(validFilm + " Фильм успешно обновлен");
        return validFilm;
    }

    @Override
    public List<Film> listFilms() {
        String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Mpa mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

    public Mpa getMpaById(int id) {
        String sqlQuery = "select * from MPA where MPA_MPA_ID =?";
        Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMPA, id);
        return mpa;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("FILM_DESCRIPTION"))
                .releaseDate(resultSet.getDate("FILM_RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("FILM_DURATION"))
                .mpa(getMpaById((int) resultSet.getInt("FILM_MPA")))
                .build();
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        checkReportExists(id);
        String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA" +
                " from FILMS where FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        return film;
    }

    public void checkReportExists(Long id) {
        String sqlQuery = "SELECT EXISTS(select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE" +
                ", FILM_DURATION, FILM_MPA from FILMS where FILM_ID = ?)";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id}, Boolean.class);
        if (exists == false) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
    }

}
