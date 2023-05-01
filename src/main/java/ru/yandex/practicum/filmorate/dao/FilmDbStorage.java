package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
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
import java.util.Optional;

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
        String sqlQuery = "insert into FILMS(FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA)" +
                " values (?, ?, ?, ?, ?)";
        if (film.getGenres() == null) {
            film.setId(fillingTheTable(film, sqlQuery));
        } else {
            film.setId(fillingTheTable(film, sqlQuery));
            String sqlQueryGenres = "insert into FILM_GENRES(FILM_GENRES_ID, FILM_GENRES_FILM_ID) values (?, ?)";
            jdbcTemplate.update(sqlQueryGenres,
                    film.getId(),
                    film.getGenres());
        }
        log.info(film + " Фильм успешно добавлен!");
        return film;
    }

    private Long fillingTheTable(Film film, String query) {
        if (film.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

//            String sqlQueryMpa = "select MPA_NAME from MPA where MPA_MPA_ID =" + film.getMpa().getId();
//            String mpaName = this.jdbcTemplate.queryForObject(
//                    sqlQueryMpa,
//                    String.class);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                if (validationFilm(film)) {
                    ps.setString(3, film.getReleaseDate().toString());
                }
                ps.setLong(4, film.getDuration());
                ps.setLong(5, film.getMpa().getId());
                return ps;
            }, keyHolder);

            return Long.parseLong(keyHolder.getKey().toString());
        } else {
            if (validationFilm(film)) {
                jdbcTemplate.update(query
                        , film.getName()
                        , film.getDescription()
                        , film.getReleaseDate()
                        , film.getDuration()
                        , film.getMpa().getId()
                        , film.getId());
            }
            return Long.parseLong(film.getId().toString());
        }
    }

    private boolean validationFilm(Film film) {

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

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?," +
                " FILM_DURATION = ?, FILM_MPA = ? where FILM_ID = ?";
        if (validationFilm(film)) {
            film.setId(Long.valueOf(fillingTheTable(film, sqlQuery).toString()));
            log.info(film + " Фильм успешно обновлен");
        }
        return film;
    }

    @Override
    public List<Film> listFilms() {
        String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA from FILMS";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        System.out.println(filmList);
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("FILM_DESCRIPTION"))
                .releaseDate(resultSet.getDate("FILM_RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("FILM_DURATION"))
                .mpa(new Mpa(resultSet.getInt("FILM_MPA")))
                .build();
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
