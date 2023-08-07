package ru.practicum.mainservice.location.service;

import ru.practicum.mainservice.location.model.Location;

public interface LocationService {
    Location addLocation(Location location);

    Location findLocationById(Long locId);
}
