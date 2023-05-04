package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

    private final UserDao userDao;

//        public UserService(UserDao userDao) {
//            this.userDao = userDao;
//        }

    public void addFriendById(Long id, Long friendId) { //PUT /users/{id}/friends/{friendId} — добавление в друзья.
        userDao.addFriendById(id, friendId);
        log.info(userDao.getUserById(id) + " теперь дружит с " + userDao.getUserById(friendId));
    }

    public void deleteFriendById(Long id, Long friendId) { //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
        //        User user1 = userDao.listUsers().stream().filter(a -> a.getId() == id).findFirst().get();
//        user1.getFriends().remove(friendId);
//        log.info("У " + user1 + " теперь в друзьях остались: " + user1.getFriends());
//        User user2 = userDao.listUsers().stream().filter(a -> a.getId() == friendId).findFirst().get();
//        user2.getFriends().remove(id);
//        log.info("У " + user2 + " теперь в друзьях остались: " + user2.getFriends());
        userDao.removeFriendById(id, friendId);
    }

    public List<User> getListFriends(Long id) { //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
        return userDao.getListFriends(id);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public List<User> getListFriendsSharedWithAnotherUser(Long id, Long otherId) {
        return userDao.getCommonFriends(id, otherId);
    }

    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public User addUser(User user) {
        return userDao.addUser(user);
    }

    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    public List<User> listUsers() {
        return userDao.listUsers();
    }
}