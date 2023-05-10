package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping({"/mpa"})
@Slf4j
@Validated
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping()
    public List<Mpa> listMpa() {
        log.info("Получаем список рейтингов, их количество: " + mpaService.listMpa().size());
        return mpaService.listMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        log.info("Получаем рейтинг по id: " + id);
        return mpaService.getMpaById(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ConstraintViolationException.class)
    public ValidationException handleException(ConstraintViolationException exception) {
        return new ValidationException(exception.getMessage());
    }
}
