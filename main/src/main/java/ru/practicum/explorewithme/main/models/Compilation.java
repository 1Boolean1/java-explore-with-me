package ru.practicum.explorewithme.main.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "Compilation_Events")
    private List<Event> events;

    @Column(nullable = false)
    @NotBlank
    private Boolean pinned;

    @Column(nullable = false)
    @NotBlank
    private String title;
}
