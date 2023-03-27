package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int id = 0;
    private boolean resultValidUser;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Добавляем пользователя: " + user);
        resultValidUser = validationUser(user);
        if (resultValidUser) {
            user.setId(++id);
            users.put(user.getId(), user);
            log.info(user + " Пользователь успешно добавлен.");
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновляем пользователя: " + user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(user + " Такой пользователь не зарегистрирован");
        }
        resultValidUser = validationUser(user);
        if (resultValidUser) {
            users.put(user.getId(), user);
            log.info(user + " Пользователь успешно обновлен.");
        }
        return user;
    }

    @GetMapping
    public List<User> listUsers() {
        log.info("Получаем список пользователей, его размер: " + users.size());
        return new ArrayList<>(users.values());
    }

    public boolean validationUser(User user) {
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
        return true;
    }
}
