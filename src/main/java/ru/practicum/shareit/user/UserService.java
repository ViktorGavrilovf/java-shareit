package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private UserStorage userStorage;

    public UserDto getUser(Long id) {
        return userStorage.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(()
                -> new NotFoundException("Пользователь с id " + id + "не найден"));
    }

    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) throw new ValidationException("Неккоректный емайл");
        if (userStorage.existsByEmail(userDto.getEmail()))
            throw new ConflictException("Пользователь с таким email уже существует");
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.save(user));
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User existing = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null) {
            if (userStorage.existsByEmail(userDto.getEmail())
                    && !existing.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
            existing.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userStorage.save(existing));
    }

    public void deleteUser(Long id) {
        getUser(id);
        userStorage.deleteById(id);
    }
}
