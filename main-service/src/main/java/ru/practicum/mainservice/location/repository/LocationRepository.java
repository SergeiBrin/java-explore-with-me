package ru.practicum.mainservice.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.location.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
