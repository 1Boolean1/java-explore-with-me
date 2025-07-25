package ru.practicum.explorewithme.main.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.main.dtos.UserCreateDto;
import ru.practicum.explorewithme.main.dtos.UserDto;
import ru.practicum.explorewithme.main.exceptions.BadRequestException;
import ru.practicum.explorewithme.main.exceptions.ExistsException;
import ru.practicum.explorewithme.main.exceptions.NotFoundException;
import ru.practicum.explorewithme.main.mappers.UserMapper;
import ru.practicum.explorewithme.main.models.User;
import ru.practicum.explorewithme.main.repositories.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto saveUser(UserCreateDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null) {
            log.error("Email or name is null");
            throw new BadRequestException("Email or name cannot be null");
        }
        if (userDto.getEmail().isBlank() || userDto.getName().isBlank()) {
            log.error("Email or name is blank");
            throw new BadRequestException("Email or name cannot be blank");
        }
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            log.error("Email already exists");
            throw new ExistsException("Email already exists");
        }

        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setName(userDto.getName());

        User savedUser = userRepository.save(newUser);

        return UserMapper.toUserDto(savedUser);
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        if (ids == null) {
            return userRepository.findAll(from, size);
        } else if (ids.isEmpty()) {
            return userRepository.findAll(from, size);
        } else {
            return userRepository.findByIds(ids, pageRequest);
        }
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User not found");
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }
}
