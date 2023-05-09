package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
@Component
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sqlQueryInsert = "insert into USERS(USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY)" +
                " values (?, ?, ?, ?)";
        Long id = writingToTableUsers(user, sqlQueryInsert);
        log.info(user + " Пользователь успешно добавлен.");
        return getUserById(id);
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        String sqlQuery = "update USERS set USER_LOGIN = ?, USER_NAME = ?, USER_EMAIL = ?, USER_BIRTHDAY = ?" +
                " where USER_ID = ?";
        writingToTableUsers(user, sqlQuery);
        log.info(user + " Пользователь успешно обновлен");
        return user;
    }

    @Override
    public List<User> listUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(Long id) {
        try {
            String sqlQuery = "select USER_ID, USER_LOGIN, USER_NAME, USER_EMAIL, USER_BIRTHDAY from USERS" +
                    " where USER_ID = ?";
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь по id: " + id + " не найден!");
        }
    }

    @Override
    public void addFriendById(Long id, Long friendId) {
        String sqlQueryInsert = "insert into FRIENDSHIP(FRIENDSHIP_USER_ID, FRIENDSHIP_FRIEND_ID)" +
                " values (?, ?)";
        User user1 = getUserById(id);
        User user2 = getUserById(friendId);
        if (!checkFriendshipExits(id, friendId)) {
            jdbcTemplate.update(sqlQueryInsert, user1.getId(), user2.getId());
            log.info("Пользователь " + user1 + " дружит с пользователем " + user2);
        }
    }

    @Override
    public List<User> getListFriends(Long id) {
        List<User> listUser = new ArrayList<>();
        String sqlQueryFriends = "select FRIENDSHIP_FRIEND_ID from FRIENDSHIP where FRIENDSHIP_USER_ID = ?";
        List<Long> listIdFriends = jdbcTemplate.queryForList(sqlQueryFriends, new Long[]{id}, Long.class);
        for (Long listIdFriend : listIdFriends) {
            listUser.add(getUserById(listIdFriend));
        }
        return listUser;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> listUser = new ArrayList<>();
        String sqlQueryFriendsUser1 = "select FRIENDSHIP_FRIEND_ID from FRIENDSHIP where FRIENDSHIP_USER_ID = ?";
        String sqlQueryFriendsUser2 = "select FRIENDSHIP_FRIEND_ID from FRIENDSHIP where FRIENDSHIP_USER_ID = ?";
        List<Long> listIdFriendsUser1 = jdbcTemplate.queryForList(sqlQueryFriendsUser1, new Long[]{id}, Long.class);
        List<Long> listIdFriendsUser2 = jdbcTemplate.queryForList(sqlQueryFriendsUser2, new Long[]{otherId},
                Long.class);
        List<Long> intersectList = listIdFriendsUser1.stream()
                .filter(listIdFriendsUser2::contains)
                .collect(Collectors.toList());
        for (Long idUser : intersectList) {
            listUser.add(getUserById(idUser));
        }
        return listUser;
    }

    @Override
    public void removeFriendById(Long id, Long friendId) {
        final String sqlQuery = "delete from FRIENDSHIP" +
                " where FRIENDSHIP_USER_ID = ? and FRIENDSHIP_FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
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
            return writingToTableUsersWithoutId(user, query);
        } else {
            return writingToTableById(user, query);
        }
    }

    private Long writingToTableUsersWithoutId(User user, String query) {
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
    }

    private Long writingToTableById(User user, String query) {
        jdbcTemplate.update(query, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(),
                user.getId());
        return Long.parseLong(user.getId().toString());
    }

    private boolean checkFriendshipExits(Long id, Long friendId) {
        String sqlQuery = "SELECT EXISTS(select * from FRIENDSHIP AS tols_user where FRIENDSHIP_USER_ID = ?" +
                " AND EXISTS (SELECT * FROM FRIENDSHIP WHERE FRIENDSHIP_FRIEND_ID= ?))";
        boolean exists = false;
        exists = jdbcTemplate.queryForObject(sqlQuery, new Long[]{id, friendId}, Boolean.class);
        if (exists) {
            return exists;
        }
        return exists;
    }

}