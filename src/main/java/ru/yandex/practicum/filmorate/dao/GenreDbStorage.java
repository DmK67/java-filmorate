package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Repository
@Slf4j
public class GenreDbStorage implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> listGenres() {
        String sqlQuery = "select * from GENRES";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenres);
    }

    @Override
    public Genre getGenreById(int id) {
        checkReportExistsMpa((long) id);
        String sqlQuery = "select * from GENRES where GENRES_GENRES_ID =?";
        Genre genre = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenres, id);
        return genre;
    }

    @Override
    public boolean checkReportExitsGenres(Long id) {
        boolean exists = false;
        String sqlQuery = "SELECT EXISTS(select * from FILM_GENRES where FILM_GENRES_ID = ?)";
        return exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id}, Boolean.class);
    }

    @Override
    public List<Genre> getListGenres(Long id) {
        List<Genre> listGenres = new ArrayList<>();
        String sqlQueryListGenres = "select FILM_GENRES_FILM_ID from FILM_GENRES where FILM_GENRES_ID = ?";
        List<Long> listIdGenres = jdbcTemplate.queryForList(sqlQueryListGenres, new Long[]{id}, Long.class);
        for (Long idGenre : listIdGenres) {
            listGenres.add(getGenreById(Math.toIntExact(idGenre)));
        }
        return listGenres;
    }

    private Genre mapRowToGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRES_GENRES_ID"))
                .name(resultSet.getString("GENRES_NAME"))
                .build();
    }

    private void checkReportExistsMpa(Long id) {
        String sqlQuery = "SELECT EXISTS(select * from GENRES where GENRES_GENRES_ID = ?)";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id}, Boolean.class);
        if (exists == false) {
            throw new NotFoundException("Жанр фильмов по id: " + id + " не найден!");
        }
    }

}
