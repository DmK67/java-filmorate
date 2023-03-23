package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    void start() {
        user = User.builder()
                .name("name")
                .email("y@ya.ru")
                .login("login")
                .birthday(LocalDate.of(1992, 12, 23))
                .build();
        userController = new UserController();
    }

    @Test
    void addUserValidation() {
        user.setName("");
        userController.addUser(user);

        assertEquals("login" , user.getName(), "Имя пользователя не записалось");

        user.setName("name");
        user.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception.getMessage());

        user.setEmail("y@ya.ru");
        user.setBirthday(LocalDate.of(9992, 12, 23));
        exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());

        user.setBirthday(LocalDate.of(2018, 12, 17));
        user.setLogin("");
        exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());

        user.setLogin("login");

        assertEquals(1, userController.getUsers().size(), "Не верное количество пользователей");
        assertNotNull(userController.getUsers(), "Список пользователей - пустой!");
    }

    @Test
    void updateUserValidation() {
        userController.addUser(user);
        user.setName("");
        userController.updateUser(user);

        assertEquals("login" , user.getName(), "Имя пользователя не записалось");

        user.setName("name");
        user.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @!", exception.getMessage());

        user.setEmail("y@ya.ru");
        user.setBirthday(LocalDate.of(9992, 12, 23));
        exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());

        user.setBirthday(LocalDate.of(2018, 12, 17));
        user.setLogin("");
        exception = assertThrows(ValidationException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());

        user.setLogin("login");

        User user2 = User.builder()
                .id(1)
                .name("name2")
                .email("y2@ya.ru")
                .login("login2")
                .birthday(LocalDate.of(1993, 12, 23))
                .build();
        userController.updateUser(user2);

        assertEquals(1, userController.getUsers().size(), "Пользователь не обновлен!");
        assertNotNull(userController.getUsers(), "Список пользователей - пустой!");
    }

    @Test
    void getlistUsers() {
        userController.addUser(user);

        assertEquals(1,userController.getUsers().size(), "Количество пользователей не соответствует!");
        assertNotNull(userController.getUsers(), "Список пользователей пустой!");
    }
}