package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Validated
@AllArgsConstructor
public class Film {
    @Min(1)
    private Long id;

    private Set<Long> likes;
    @NotBlank
    private String name;

    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private List<Genre> genres;

    public void setMpa(Mpa mpa) {
        this.mpa = mpa;
    }

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
