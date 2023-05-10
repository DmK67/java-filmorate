package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Repository
@Slf4j
@AllArgsConstructor
public class MpaDbStorage implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> listMpa() {
        String sqlQuery = "select * from MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "select * from MPA where MPA_MPA_ID =?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMPA, id);
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг по id: " + id + " не найден!");
        }
    }

    private Mpa mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

}
