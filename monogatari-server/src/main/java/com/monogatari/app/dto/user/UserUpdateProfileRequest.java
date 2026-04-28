package com.monogatari.app.dto.user;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateProfileRequest {
    @Size(min = 3, max = 255, message = "Username must be between 3 and 255 characters!")
    @Pattern(
          regexp = "^[\\p{L}0-9 ._-]+$",
          message = "Username can only contains letters, numbers, dots, hyphens, and underscores!"
          )
    private String username;

    @Past(message = "Birth date must be in the past!")
    private LocalDate birthDate;
}