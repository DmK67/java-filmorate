package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public void addFriendById(@Valid Long id, @Valid Long friendId) { //PUT /users/{id}/friends/{friendId} — добавление в друзья.
        User user1 = inMemoryUserStorage.getUsers().get(id);
        user1.setFriends(friendId);
        log.info("У " + user1 + " теперь в друзьях: " + user1.getFriends());
        User user2 = inMemoryUserStorage.getUsers().get(friendId);
        user2.setFriends(id);
        log.info("У " + user2 + " теперь в друзьях: " + user2.getFriends());
    }

    public void deleteFriendById(@Valid Long id, Long friendId) { //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
        User user1 = inMemoryUserStorage.getUsers().get(id);
        user1.getFriends().remove(friendId);
        log.info("У " + user1 + " теперь в друзьях остались: " + user1.getFriends());
        User user2 = inMemoryUserStorage.getUsers().get(friendId);
        user2.getFriends().remove(id);
        log.info("У " + user2 + " теперь в друзьях остались: " + user2.getFriends());
    }

    public Set<User> getListFriends(@Valid Long id) { //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
        Set<User> listFriends = new HashSet<User>();
        User user = inMemoryUserStorage.getUsers().get(id);
        for (Long friend : user.getFriends()) {
            listFriends.add(inMemoryUserStorage.getUsers().get(friend));
        }
        return listFriends;
    }

    public Set<User> getListFriendsSharedWithAnotherUser(@Valid Long id, Long otherId) { //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
        Set<User> crossingFriendsTotal = new HashSet<User>();
        User user1 = inMemoryUserStorage.getUsers().get(id);
        User user2 = inMemoryUserStorage.getUsers().get(otherId);
        Set<Long> crossingFriends = new HashSet<Long>((user1.getFriends()).stream()
                .filter((user2.getFriends())::contains).collect(Collectors.toSet()));
        for (Long crossingFriend : crossingFriends) {
            crossingFriendsTotal.add(inMemoryUserStorage.getUsers().get(crossingFriend));
        }
        return crossingFriendsTotal;
    }

    public User getUserById(@Valid Long id) {
        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден");
        }
        return inMemoryUserStorage.getUsers().get(id);
    }
}
