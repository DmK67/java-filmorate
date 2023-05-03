package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendsStatus {
    private Long idStatus;
    //@NotBlank
    private String nameStatus;

}
