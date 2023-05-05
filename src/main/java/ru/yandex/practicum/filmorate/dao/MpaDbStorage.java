package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
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
public class MpaDbStorage implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> listMpa() {
        String sqlQuery = "select * from MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
    }

    @Override
    public Mpa getMpaById(int id) {
        checkReportExistsMpa((long) id);
        String sqlQuery = "select * from MPA where MPA_MPA_ID =?";
        Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMPA, id);
        return mpa;
    }

    private Mpa mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

    private void checkReportExistsMpa(Long id) {
        String sqlQuery = "SELECT EXISTS(select * from MPA where MPA_MPA_ID = ?)";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id}, Boolean.class);
        if (exists == false) {
            throw new NotFoundException("Рейтинг фильмов по id: " + id + " не найден!");
        }
    }

}
