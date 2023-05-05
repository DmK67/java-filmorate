package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class MpaService {
    private final MpaDao mpaDao;

    public List<Mpa> listMpa() {
        return mpaDao.listMpa();
    }

    public Mpa getMpaById(Long id) {
        return mpaDao.getMpaById(Math.toIntExact(id));
    }
}
