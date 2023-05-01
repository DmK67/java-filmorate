package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    //Optional<User> findUserById(String id);

    User addUser(User user);

    User updateUser(User user);

    List<User> listUsers();

    User getUserById(Long id);
}
