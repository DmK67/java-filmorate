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
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Component
@Repository
@Slf4j
public class FilmDbStorage implements FilmDao {

    private final MpaDao mpaDao;

    private final GenreDao genreDao;

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(MpaDao mpaDao, GenreDao genreDao, JdbcTemplate jdbcTemplate) {
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        Film validFilm = validationFilm(film);
        final String sqlQueryInsert = "insert into FILMS(FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA)" +
                " values (?, ?, ?, ?, ?)";
        Long id = writingToTable(validFilm, sqlQueryInsert);
        log.info(getFilmById(id) + " Фильм успешно добавлен!");
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
            if (film.getGenres().size() > 0) {
                for (Genre genreId : film.getGenres()) {
                    final String sqlQueryFilmGenres = "insert into FILM_GENRES(FILM_GENRES_ID, FILM_GENRES_FILM_ID)" +
                            " values (?, ?)";
                    jdbcTemplate.update(sqlQueryFilmGenres
                            , Long.parseLong(keyHolder.getKey().toString())
                            , genreId.getId());
                }
            }
            return Long.parseLong(keyHolder.getKey().toString());
        } else {
            jdbcTemplate.update(query
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getId());
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
            if (film.getGenres().size() > 0) {
                final String sqlQueryListGenges = "select FILM_GENRES_FILM_ID FILM_GENRES from FILM_GENRES" +
                        " where FILM_GENRES_ID = ?";
                List<Long> listIdGenres = jdbcTemplate.queryForList(sqlQueryListGenges
                        , new Long[]{Long.parseLong(film.getId().toString())}, Long.class);

                for (Long idGenre : listIdGenres) {
                    final String sqlQueryGenreDeleteById = "delete from FILM_GENRES where FILM_GENRES_ID = ?" +
                            " and FILM_GENRES_FILM_ID = ?";
                    jdbcTemplate.update(sqlQueryGenreDeleteById
                            , Long.parseLong(film.getId().toString())
                            , idGenre);
                }
                Set<Long> myList = new HashSet<Long>();
                for (Genre genreId : film.getGenres()) {
                    myList.add((long) genreId.getId());
                }
                final String sqlQueryFilmGenres = "insert into FILM_GENRES(FILM_GENRES_ID, FILM_GENRES_FILM_ID)" +
                        " values (?, ?)";
                for (Long aLong : myList) {
                    jdbcTemplate.update(sqlQueryFilmGenres
                            , film.getId()
                            , aLong);
                }
            } else {
                final String sqlQueryListGenges = "select FILM_GENRES_FILM_ID FILM_GENRES from FILM_GENRES" +
                        " where FILM_GENRES_ID = ?";
                List<Long> listIdGenres = jdbcTemplate.queryForList(sqlQueryListGenges
                        , new Long[]{Long.parseLong(film.getId().toString())}, Long.class);
                for (Long idGenre : listIdGenres) {
                    final String sqlQueryGenreDeleteById = "delete from FILM_GENRES where FILM_GENRES_ID = ?" +
                            " and FILM_GENRES_FILM_ID = ?";
                    jdbcTemplate.update(sqlQueryGenreDeleteById
                            , Long.parseLong(film.getId().toString())
                            , idGenre);
                }
            }
            return Long.parseLong(film.getId().toString());
        }
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

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        Film validFilm = validationFilm(film);
        final String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?," +
                " FILM_DURATION = ?, FILM_MPA = ? where FILM_ID = ?";
        Long id = writingToTable(validFilm, sqlQuery);
        film = getFilmById(id);
        log.info(validFilm + " Фильм успешно обновлен");
        return film;
    }

    @Override
    public List<Film> listFilms() {
        final String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA from FILMS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    private Genre mapRowToGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRES_GENRES_ID"))
                .name(resultSet.getString("GENRES_NAME"))
                .build();
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("FILM_DESCRIPTION"))
                .releaseDate(resultSet.getDate("FILM_RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("FILM_DURATION"))
                .mpa(mpaDao.getMpaById((int) resultSet.getInt("FILM_MPA")))
                .genres(new ArrayList<>())
                .build();
        if (genreDao.checkReportExitsGenres(film.getId())) {
            List<Genre> listGenresFilm = genreDao.getListGenres(film.getId());
            film.setGenres(listGenresFilm);
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        checkReportExistsFilm(id);
        final String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_MPA" +
                " from FILMS where FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        return film;
    }

    private void checkReportExistsFilm(Long id) {
        final String sqlQuery = "SELECT EXISTS(select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE" +
                ", FILM_DURATION, FILM_MPA from FILMS where FILM_ID = ?)";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id}, Boolean.class);
        if (exists == false) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
    }

    @Override
    public void addLikeFilmToUser(Long id, Long userId) {
        final String sqlQueryInsertLike = "insert into FILM_LIKES(FILMS_LIKES_ID, FILM_LIKES_USER_ID_WHO_LIKE_FILM)" +
                " values (?, ?)";
        jdbcTemplate.update(sqlQueryInsertLike
                , id
                , userId);
    }

    @Override
    public void deleteLikeFilmToUser(Long id, Long userId) {
        final String sqlQuery = "delete from FILM_LIKES" +
                " where FILMS_LIKES_ID = ? and FILM_LIKES_USER_ID_WHO_LIKE_FILM = ?";
        jdbcTemplate.update(sqlQuery
                , id
                , userId);
    }
}
