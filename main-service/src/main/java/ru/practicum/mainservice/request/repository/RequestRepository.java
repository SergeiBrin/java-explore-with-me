package ru.practicum.mainservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.request.enums.RequestStatus;
import ru.practicum.mainservice.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    Long countByEventId(Long eventId);

    Long countByIdInAndStatus(List<Long> ids, RequestStatus status);

    List<Request> findByRequesterId(Long userId);

    List<Request> findByIdIn(List<Long> requestIds);

    @Query("SELECT r " +
            "FROM Request as r " +
            "JOIN Event as e ON r.event.id = e.id " +
            "WHERE e.initiator.id = :userId " +
            "AND r.event.id = :eventId")
    List<Request> findByInitiatorIdAndEventId(Long userId, Long eventId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);
}
