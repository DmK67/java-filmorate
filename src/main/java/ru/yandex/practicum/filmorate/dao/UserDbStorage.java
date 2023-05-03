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
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
@Component
@Slf4j
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        User validUser = validationFilm(user);
        String sqlQueryInsert = "insert into USERS(USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY)" +
                " values (?, ?, ?, ?)";
        Long id = writingToTableUsers(validUser, sqlQueryInsert);
        log.info(validUser + " Пользователь успешно добавлен.");
        return getUserById(id);
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        User validUser = validationFilm(user);
        String sqlQuery = "update USERS set USER_LOGIN = ?, USER_NAME = ?, USER_EMAIL = ?, USER_BIRTHDAY = ? where USER_ID = ?";
        writingToTableUsers(validUser, sqlQuery);
        log.info(validUser + " Пользователь успешно обновлен");
        return validUser;
    }

    @Override
    public List<User> listUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User validationFilm(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info(user.getBirthday() + " Ошибка! Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя для отображения пустое — в таком случае будет используем логин.");
        }
        if (user.getLogin().trim().contains(" ")) {
            log.info(user.getLogin() + " Ошибка! Логин не может быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        checkReportExistsUser(id);
        String sqlQuery = "select USER_ID, USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY from USERS where USER_ID = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        return user;
    }

    @Override
    public void addFriendById(Long id, Long friendId) {
        String sqlQueryInsert = "insert into FRIENDSHIP(FRIENDSHIP_USER_ID, FRIENDSHIP_FRIEND_ID, FRIENDSHIP_STATUS" +
                " values (?, ?, 1)";
        User user1 = getUserById(id);
        User user2 = getUserById(friendId);
        jdbcTemplate.update(sqlQueryInsert
                , user1.getId()
                , user2.getId());
    }

    public void checkReportExistsUser(Long id) {
        String sqlQuery = "SELECT EXISTS(select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY from USERS where USER_ID = ?)";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id}, Boolean.class);
        if (exists == false) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("USER_EMAIL"))
                .login(resultSet.getString("USER_LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .birthday(resultSet.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
        return user;
    }

    private Long writingToTableUsers(User user, String query) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, RETURN_GENERATED_KEYS);
                ps.setString(1, user.getLogin());
                ps.setString(2, user.getName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getBirthday().toString());
                return ps;
            }, keyHolder);
            return Long.parseLong(keyHolder.getKey().toString());
        } else {
            jdbcTemplate.update(query
                    , user.getLogin()
                    , user.getName()
                    , user.getEmail()
                    , user.getBirthday()
                    , user.getId());
            return Long.parseLong(user.getId().toString());
        }
    }
}
