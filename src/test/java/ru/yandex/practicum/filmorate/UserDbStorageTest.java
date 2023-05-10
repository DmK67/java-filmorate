package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @DirtiesContext
    @Test
    void addUserTest() throws ValidationException, NotFoundException {
        User user1 = User.builder()
                .email("Pobeda@ya.ru")
                .login("PobedaLogin")
                .name("PobedaName")
                .birthday(LocalDate.of(1945, 5, 9))
                .build();
        User user2 = User.builder()
                .email("Pobeda@ya.ru")
                .login("PobedaLogin")
                .birthday(LocalDate.of(1945, 5, 9))
                .build();
        user2.setId(1L);
        user2.setName("PobedaName");
        userDbStorage.addUser(user1);
        User user3 = userDbStorage.getUserById(1L);
        assertEquals(user2.getId(), user3.getId());
        assertEquals(user2.getName(), user3.getName());
        assertEquals(user2.getLogin(), user3.getLogin());
        assertEquals(user2.getEmail(), user3.getEmail());
        assertEquals(user2.getBirthday(), user3.getBirthday());
    }

    @DirtiesContext
    @Test
    void updateUserTest() throws ValidationException, NotFoundException {
        User user1 = User.builder()
                .email("Pobeda@ya.ru")
                .login("PobedaLogin")
                .name("PobedaName")
                .birthday(LocalDate.of(1945, 5, 9))
                .build();
        userDbStorage.addUser(user1);
        User user2 = User.builder()
                .email("Pobeda@ya.ru")
                .login("PobedaLogin")
                .birthday(LocalDate.of(1945, 5, 9))
                .build();
        user2.setId(1L);
        user2.setName("PobedaName");
        userDbStorage.updateUser(user2);
        User user3 = userDbStorage.getUserById(1L);
        assertEquals(user2.getId(), user3.getId());
        assertEquals(user2.getName(), user3.getName());
        assertEquals(user2.getLogin(), user3.getLogin());
        assertEquals(user2.getEmail(), user3.getEmail());
        assertEquals(user2.getBirthday(), user3.getBirthday());
    }

    @DirtiesContext
    @Test
    void getUserList() throws ValidationException {
        List<User> users = userDbStorage.listUsers();
        assertEquals(0, users.size());
        User user1 = User.builder()
                .email("pobeda@ya.ru")
                .login("pobedaLogin")
                .name("PobedaName")
                .birthday(LocalDate.of(1945, 5, 9))
                .build();
        userDbStorage.addUser(user1);
        users = userDbStorage.listUsers();
        assertEquals(1, users.size());
    }

}
