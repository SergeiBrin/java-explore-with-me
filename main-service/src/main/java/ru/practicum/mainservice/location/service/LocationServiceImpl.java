package ru.practicum.mainservice.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.location.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public Location addLocation(Location location) {
        Location addLocation = locationRepository.save(location);
        log.info("Запрос в метод addLocation() обработан успешно, location={}, addLocation={}", location, addLocation);

        return addLocation;
    }

    @Transactional(readOnly = true)
    @Override
    public Location findLocationById(Long locId) {
        Location findLocation = locationRepository.findById(locId)
                .orElseThrow(() -> new NotFoundException(String.format("Location with id=%d was not found", locId)));
        log.info("Запрос в метод findLocationById() обработан успешно, locId={}, findLocation={}", locId, findLocation);

        return findLocation;
    }
}
