package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(Long userId);

    List<User> findAllUsers();

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    Optional<User> findByEmail(String email);
}
