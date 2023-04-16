package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Validated
public class Film {
    @Min(1)
    private Long id;

    private Set<Long> likes;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    public void setLikes(Long id) {
        if (likes == null) {
            likes = new HashSet<Long>();
            likes.add(id);
        } else {
            likes.add(id);
        }
    }

    public Set<Long> getLikes() {
        if (likes == null) {
            likes = new HashSet<Long>();
        }
        return likes;
    }
}
