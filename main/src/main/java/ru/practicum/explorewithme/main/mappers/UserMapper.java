package ru.practicum.explorewithme.main.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.dtos.UserDto;
import ru.practicum.explorewithme.main.dtos.UserShortDto;
import ru.practicum.explorewithme.main.models.User;

@UtilityClass
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
