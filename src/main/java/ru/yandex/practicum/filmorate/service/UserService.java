package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {
    private final UserStorage userStorage;

    public void addFriendById(Long id, Long friendId) { //PUT /users/{id}/friends/{friendId} — добавление в друзья.
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);
        user1.setFriends(friendId);
        user2.setFriends(id);
        log.info("У " + user1 + " теперь в друзьях: " + user1.getFriends());
        log.info("У " + user2 + " теперь в друзьях: " + user2.getFriends());
    }

    public void deleteFriendById(Long id, Long friendId) { //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
        User user1 = userStorage.listUsers().stream().filter(a -> a.getId() == id).findFirst().get();
        user1.getFriends().remove(friendId);
        log.info("У " + user1 + " теперь в друзьях остались: " + user1.getFriends());
        User user2 = userStorage.listUsers().stream().filter(a -> a.getId() == friendId).findFirst().get();
        user2.getFriends().remove(id);
        log.info("У " + user2 + " теперь в друзьях остались: " + user2.getFriends());
    }

    public Set<User> getListFriends(Long id) { //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
        Set<User> listFriends = new HashSet<User>();
        User user = userStorage.listUsers().stream().filter(a -> a.getId() == id).findFirst().get();
        for (Long friend : user.getFriends()) {
            listFriends.add(userStorage.listUsers().stream().filter(a -> a.getId() == friend).findFirst().get());
        }
        return listFriends;
    }

    public Set<User> getListFriendsSharedWithAnotherUser(Long id, Long otherId) { //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
        Set<User> crossingFriendsTotal = new HashSet<User>();
        User user1 = userStorage.listUsers().stream().filter(a -> a.getId() == id).findFirst().get();
        User user2 = userStorage.listUsers().stream().filter(a -> a.getId() == otherId).findFirst().get();
        Set<Long> crossingFriends = new HashSet<Long>((user1.getFriends()).stream()
                .filter((user2.getFriends())::contains).collect(Collectors.toSet()));
        for (Long crossingFriend : crossingFriends) {
            crossingFriendsTotal.add(userStorage.listUsers().stream().filter(a -> a.getId() == crossingFriend)
                    .findFirst().get());
        }
        return crossingFriendsTotal;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> listUsers() {
        return userStorage.listUsers();
    }
}