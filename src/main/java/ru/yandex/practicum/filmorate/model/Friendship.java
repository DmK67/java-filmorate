package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    private Long user_id;
    private Long friend_id;
    private FriendsStatus status;
}
