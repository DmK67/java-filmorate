package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
//@Validated
public class Genre {
    //@Min(1)
    private Long id;
    //@NotBlank
    private String name;

    public Genre(Long id) {
        this.id = id;
    }
}
