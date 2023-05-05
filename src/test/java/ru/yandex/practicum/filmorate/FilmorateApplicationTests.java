package ru.yandex.practicum.filmorate;

import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.ObjectAssert;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static ch.qos.logback.classic.spi.ThrowableProxyVO.build;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;

    User user = User.builder()
            .name("nameTest")
            .email("test@mail.ru")
            .login("loginTest")
            .birthday(LocalDate.of(1982, 05, 8))
            .build();

    User userFriend = User.builder()
            .name("nameTestFriend")
            .email("testFriend@mail.ru")
            .login("loginTestFriend")
            .birthday(LocalDate.of(1985, 10, 28))
            .build();

    User newUser = User.builder()
            .id(1L)
            .name("nameTestUpdate")
            .email("testUpdate@mail.ru")
            .login("loginTestUpdate")
            .birthday(LocalDate.of(1982, 05, 8))
            .build();


    @Test
    public void testFindUserById() {
        userStorage.addUser(user);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }


    @Test
    public void testUpdateUser() {
        userStorage.addUser(user);
        userStorage.updateUser(newUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "nameTestUpdate")
                );
    }

    @Test
    public void testListUser() {
        userStorage.addUser(user);

        List<User> userOptional = userStorage.listUsers();

        assertThat(userOptional).isNotNull();
    }

    @Test
    public void testAddfriend() {
        userStorage.addUser(user);
        userStorage.addUser(userFriend);
        userStorage.addFriendById(1L, 2L);
        List<User> friends = userStorage.getListFriends(1L);

        assertThat(friends).isNotNull();
    }


}
