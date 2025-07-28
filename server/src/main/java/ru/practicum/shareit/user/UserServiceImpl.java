package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public UserDto getUser(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(()
                -> new NotFoundException("Пользователь с id " + id + "не найден"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) throw new ValidationException("Неккоректный емайл");
        if (userRepository.existsByEmail(userDto.getEmail()))
            throw new ConflictException("Пользователь с таким email уже существует");
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmail(userDto.getEmail())
                    && !existing.getEmail().equals(userDto.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
            existing.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.save(existing));
    }

    @Override
    public void deleteUser(Long id) {
        getUser(id);
        userRepository.deleteById(id);
    }
}
