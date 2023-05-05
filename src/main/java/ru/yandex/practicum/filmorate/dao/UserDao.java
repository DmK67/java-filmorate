package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);

    User updateUser(User user);

    List<User> listUsers();

    User getUserById(Long id);

    void addFriendById(Long id, Long friendId);

    List<User> getListFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);

    void removeFriendById(Long id, Long friendId);
}
