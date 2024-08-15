package ru.yandex.practicum.filmorate.service;

import exception.ConditionsNotMetException;
import exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@AllArgsConstructor
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            RuntimeException exception = new ConditionsNotMetException("id должен быть указан");
            log.error("ошибка обновления фильма.", exception);
            throw exception;
        }

        Film oldFilm = filmStorage.getById(film.getId());

        if (oldFilm == null) {
            RuntimeException exception = new NotFoundException("не найден фильм с id = " + film.getId());
            log.error("ошибка обновления фильма.", exception);
            throw exception;
        }

        film.setLikes(oldFilm.getLikes());

        log.info("обновлен фильм {}", film);

        return filmStorage.update(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            RuntimeException exception = new NotFoundException("не найден фильм с id = " + filmId);
            log.error("ошибка добавления лайка фильма.", exception);
            throw exception;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            RuntimeException exception = new NotFoundException("не найден пользователь с id = " + userId);
            log.error("ошибка добавления лайка фильма.", exception);
            throw exception;
        }


        film.addLike(userId);


        filmStorage.update(film);

        log.info("лайк добавлен {}", film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            RuntimeException exception = new NotFoundException("не найден фильм с id = " + filmId);
            log.error("ошибка удаления лайка фильма.", exception);
            throw exception;
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            RuntimeException exception = new NotFoundException("не найден пользователь с id = " + userId);
            log.error("ошибка удаления лайка фильма.", exception);
            throw exception;
        }

        film.removeLike(userId);

        filmStorage.update(film);

        log.info("лайк удален {}", film);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
