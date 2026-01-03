package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (isEmailAlreadyUsed(user)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User savedUser = users.get(newUser.getId());
            if (!savedUser.getEmail().equals(newUser.getEmail()) && isEmailAlreadyUsed(newUser)) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            if (newUser.getEmail() != null) {
                savedUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() != null) {
                savedUser.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() != null) {
                savedUser.setPassword(newUser.getPassword());
            }
            return savedUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private boolean isEmailAlreadyUsed(User user) {
        return users.values().stream()
                .anyMatch(savedUser -> savedUser.getEmail().equals(user.getEmail()));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
