package ru.practicum.explorewithme.main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.gateway.client.StatsClient;
import ru.practicum.explorewithme.main.dtos.UserCreateDto;
import ru.practicum.explorewithme.main.dtos.UserDto;
import ru.practicum.explorewithme.main.services.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;
    private final StatsClient statsClient;

    public UserController(UserService userService, StatsClient statsClient) {
        this.userService = userService;
        this.statsClient = statsClient;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateDto user, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        return userService.saveUser(user);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size,
                                  HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, HttpServletRequest request) {
        statsClient.saveStats("explore-with-me", request.getRequestURI(), request.getRemoteAddr());
        userService.deleteUser(id);
    }
}
