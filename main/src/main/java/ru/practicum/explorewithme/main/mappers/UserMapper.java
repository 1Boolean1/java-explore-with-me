package ru.practicum.explorewithme.main.mappers;

import ru.practicum.explorewithme.main.dtos.UserDto;
import ru.practicum.explorewithme.main.models.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User fromUserDto(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
