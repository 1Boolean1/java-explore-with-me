package ru.practicum.explorewithme.main.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Min(1)
    private Long id;
    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;
}
