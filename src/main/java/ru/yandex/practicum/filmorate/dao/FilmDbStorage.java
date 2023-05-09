package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Component
@Repository
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmDao {

    private final MpaDao mpaDao;

    private final GenreDao genreDao;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        final String sqlQueryInsert = "insert into FILMS(FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE" +
                ", FILM_DURATION, FILM_MPA)" +
                " values (?, ?, ?, ?, ?)";
        Long id = writingToTable(film, sqlQueryInsert);
        log.info(getFilmById(id) + " Фильм успешно добавлен!");
        return getFilmById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        final String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?," +
                " FILM_DURATION = ?, FILM_MPA = ? where FILM_ID = ?";
        Long id = writingToTable(film, sqlQuery);
        film = getFilmById(id);
        log.info(film + " Фильм успешно обновлен");
        return film;
    }

    @Override
    public List<Film> listFilms() {
        final String sqlQuery = "SELECT FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_MPA, MPA_NAME FROM FILMS F JOIN MPA M on M.MPA_MPA_ID = F.FILM_MPA";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        Map<Long, Film> filmMap = new HashMap<>();

        for (Film film : filmList) {
            filmMap.put(film.getId(), film);
        }
        genreDao.setGenresForFilms(filmMap);
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            final String sqlQuery = "SELECT FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                    "FILM_MPA, MPA_NAME FROM FILMS INNER JOIN MPA ON FILMS.FILM_MPA = MPA.MPA_MPA_ID WHERE FILM_ID=?";
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            film.setGenres(genreDao.getListGenresByMovieId(id));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм по id: " + id + " не найден!");
        }
    }

    @Override
    public void addLikeFilmToUser(Long id, Long userId) {
        final String sqlQueryInsertLike = "insert into FILM_LIKES(FILMS_LIKES_ID, FILM_LIKES_USER_ID_WHO_LIKE_FILM)" +
                " values (?, ?)";
        jdbcTemplate.update(sqlQueryInsertLike, id, userId);
    }

    @Override
    public void deleteLikeFilmToUser(Long id, Long userId) {
        final String sqlQuery = "delete from FILM_LIKES" +
                " where FILMS_LIKES_ID = ? and FILM_LIKES_USER_ID_WHO_LIKE_FILM = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    private Long writingToTable(Film film, String query) {
        if (film.getId() == null) {
            return writingToTableWithoutId(film, query);
        } else {
            return writingToTableById(film, query);
        }
    }

    private Long writingToTableWithoutId(Film film, String query) {
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
            final String sqlQuery = "INSERT INTO FILM_GENRES(FILM_GENRES_FILM_ID, FILM_GENRES_GENRES_ID) VALUES ( ?, ? );";
            jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, Long.parseLong(keyHolder.getKey().toString()));
                    ps.setLong(2, film.getGenres().get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return film.getGenres().size();
                }
            });
        }
        return Long.parseLong(keyHolder.getKey().toString());
    }

    private Long writingToTableById(Film film, String query) {
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        if (film.getGenres().size() > 0) {

            final String sqlQueryListGenges = "select FILM_GENRES_GENRES_ID from FILM_GENRES" +
                    " where FILM_GENRES_FILM_ID = ?";
            List<Long> listIdGenres = jdbcTemplate.queryForList(sqlQueryListGenges,
                    new Long[]{Long.parseLong(film.getId().toString())}, Long.class);
            for (Long idGenre : listIdGenres) {
                final String sqlQueryGenreDeleteById = "delete from FILM_GENRES where FILM_GENRES_FILM_ID = ?" +
                        " and FILM_GENRES_GENRES_ID = ?";
                jdbcTemplate.update(sqlQueryGenreDeleteById, Long.parseLong(film.getId().toString()), idGenre);
            }

            Set<Long> myList = new HashSet<Long>();
            for (Genre genreId : film.getGenres()) {
                myList.add((long) genreId.getId());
            }
            final String sqlQueryFilmGenres = "insert into FILM_GENRES(FILM_GENRES_FILM_ID, FILM_GENRES_GENRES_ID)" +
                    " values (?, ?)";
            for (Long aLong : myList) {
                jdbcTemplate.update(sqlQueryFilmGenres, film.getId(), aLong);
            }
        } else {
            final String sqlQueryListGenges = "select FILM_GENRES_GENRES_ID from FILM_GENRES" +
                    " where FILM_GENRES_FILM_ID = ?";
            List<Long> listIdGenres = jdbcTemplate.queryForList(sqlQueryListGenges,
                    new Long[]{Long.parseLong(film.getId().toString())}, Long.class);
            for (Long idGenre : listIdGenres) {
                final String sqlQueryGenreDeleteById = "delete from FILM_GENRES where FILM_GENRES_FILM_ID = ?" +
                        " and FILM_GENRES_GENRES_ID = ?";
                jdbcTemplate.update(sqlQueryGenreDeleteById, Long.parseLong(film.getId().toString()), idGenre);
            }
        }
        return Long.parseLong(film.getId().toString());
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("FILM_DESCRIPTION"))
                .releaseDate(resultSet.getDate("FILM_RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("FILM_DURATION"))
                .mpa(new Mpa(resultSet.getInt("FILM_MPA"), resultSet.getString("MPA_NAME")))
                .build();
        return film;
    }

}
