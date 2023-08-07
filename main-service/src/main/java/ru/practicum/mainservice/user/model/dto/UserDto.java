package ru.practicum.mainservice.user.model.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    private String email;

    private Long id;

    private String name;
}
