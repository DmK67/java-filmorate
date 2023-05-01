package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Validated
public class User {
    @Min(1)
    private Long id;
    private Set<Long> friends;

    public void setFriends(Long id) {
        if (friends == null) {
            friends = new HashSet<Long>();
            friends.add(id);
        } else {
            friends.add(id);
        }
    }

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<Long>();
        }
        return friends;
    }

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private String friendship;
    private String friendshipTrue;
    private String friendshipFalse;


}
