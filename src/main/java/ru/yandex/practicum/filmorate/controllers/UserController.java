package ru.yandex.practicum.filmorate.controllers;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    public Map<Integer, User> getUsers() {
        return users;
    }

    private Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @PostMapping
    public User addUser(@NonNull @RequestBody User user) {
        log.info("Добавляем пользователя: " + user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя для отображения пустое — в таком случае будет используем логин.");
        }
        if ((user.getEmail().isEmpty() || user.getEmail().isBlank()) || (!user.getEmail().contains("@"))) {
            log.info(user.getEmail() + " Ошибка! Электронная почта не может быть пустой и должна содержать символ @!");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getLogin().isEmpty() || user.getLogin().trim().isBlank()) {
            log.info(user.getLogin() + " Ошибка! Логин не может быть пустым и содержать пробелы!");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info(user.getBirthday() + " Ошибка! Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info(user + " Пользователь успешно добавлен.");
        return user;
    }

    @PutMapping
    public User updateUser(@NonNull @RequestBody User user) {
        log.info("Обновляем пользователя: " + user);
        if ((user.getEmail().isEmpty() || user.getEmail().isBlank()) || (!user.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @!");
        }
        if (user.getLogin().isEmpty() || user.getLogin().trim().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException(user + " Такой пользователь не зарегистрирован");
        }
        users.put(user.getId(), user);
        log.info(user + " Пользователь успешно обновлен.");
        return user;
    }

    @GetMapping
    public List<User> listUsers() {
        log.info("Получаем список пользователей, его размер: " + users.size());
        return new ArrayList<>(users.values());
    }

}
