package ru.practicum.statservice.mapper;

import ru.practicum.statdto.dto.EndpointHitDto;
import ru.practicum.statservice.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit buildEndpointHit(EndpointHitDto hitDto) {
        return EndpointHit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }
}
