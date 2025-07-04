package ru.practicum.explorewithme.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hit")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank
    private String app;

    @Column(nullable = false)
    @NotBlank
    private String uri;

    @Column(nullable = false)
    @NotBlank
    private String ip;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime time;
}
