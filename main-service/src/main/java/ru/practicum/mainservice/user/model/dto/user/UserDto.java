package ru.practicum.mainservice.user.model.dto.user;

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

    private boolean privateAccount;
}
