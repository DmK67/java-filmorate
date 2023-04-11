package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    public Map<Long, User> getUsers() {
        return users;
    }

    private Long id = 0L;
    private boolean resultValidUser;

    @Override
    public User addUser(User user) {
        resultValidUser = validationUser(user);
        if (resultValidUser) {
            user.setId(++id);
            users.put(user.getId(), user);
            log.info(user + " Пользователь успешно добавлен.");
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
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

    @Override
    public List<User> listUsers() {
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
