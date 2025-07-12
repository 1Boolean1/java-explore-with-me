package ru.practicum.explorewithme.main.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.dtos.UserDto;
import ru.practicum.explorewithme.main.models.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT new ru.practicum.explorewithme.main.dtos.UserDto(u.id, u.email, u.name) " +
            "FROM User u " +
            "WHERE u.id IN ?1 " +
            "ORDER BY u.id " +
            "LIMIT ?3 OFFSET ?2")
    List<UserDto> findByIds(List<Long> ids, Pageable pageable);

    @Query("SELECT new ru.practicum.explorewithme.main.dtos.UserDto(u.id, u.email, u.name) " +
            "FROM User u " +
            "ORDER BY u.id " +
            "LIMIT ?2 OFFSET ?1")
    List<UserDto> findAll(int from, int size);
}
