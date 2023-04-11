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
        //userController = new UserController(inMemoryUserStorage);
    }

    @Test
    void validationUserTests() {
        user.setBirthday(LocalDate.of(9992, 12, 23));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());

        user.setBirthday(LocalDate.of(2018, 12, 17));
        user.setName("");

        assertEquals("login", user.getLogin(), "Имя не соответствует!");

        user.setName("name");
        user.setLogin("name login");
        exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());

        user.setLogin("login");
    }

    @Test
    void getlistUsers() {
        userController.addUser(user);

        assertEquals(1, userController.listUsers().size(), "Количество пользователей не соответствует!");
        assertNotNull(userController.listUsers(), "Список пользователей пустой!");
    }
}