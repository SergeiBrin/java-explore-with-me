package ru.practicum.statdto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
