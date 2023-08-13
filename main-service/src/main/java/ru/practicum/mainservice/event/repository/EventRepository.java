package ru.practicum.mainservice.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.event.enums.State;
import ru.practicum.mainservice.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long eventId, State state);

    List<Event> findByInitiatorId(Long userId, Pageable page);

    List<Event> findByIdIn(Set<Long> eventIds);

    List<Event> findByInitiatorIdAndStateAndEventDateAfter(Long userId, State state, LocalDateTime dateTime, Pageable page);

    // Admin GET events со временем
    @Query("SELECT e " +
            "FROM Event as e " +
            "JOIN e.initiator as u " +
            "JOIN e.category as c " +
            "WHERE (:users IS NULL OR u.id IN :users) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (e.createdOn BETWEEN :rangeStart AND :rangeEnd)")
    List<Event> findEventsByParametersWithTime(List<Long> users,
                                               List<State> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Pageable page);

    // Admin GET events без времени
    @Query("SELECT e " +
            "FROM Event as e " +
            "JOIN e.initiator as u " +
            "JOIN e.category as c " +
            "WHERE (:users IS NULL OR u.id IN :users) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (:states IS NULL OR e.state IN :states)")
    List<Event> findEventsByParametersWithoutTime(@Param("users") List<Long> users,
                                                  @Param("states") List<State> states,
                                                  @Param("categories")List<Long> categories,
                                                  Pageable page);

    // Публичный GET events со временем
    @Query("SELECT e " +
            "FROM Event as e " +
            "JOIN e.category as c " +
            "LEFT JOIN Request as r ON e.id = r.event.id " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "GROUP BY e.id " +
            "HAVING (:onlyAvailable = false OR COUNT(r) < e.participantLimit)")
    List<Event> findPublishedEventsByParameters(@Param("text") String text,
                                                @Param("categories") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                @Param("onlyAvailable") Boolean onlyAvailable,
                                                Pageable pageable);

    // Публичный GET events без передаваемого интервала времени
    // Время от now() - CURRENT_TIMESTAMP
    @Query("SELECT e " +
            "FROM Event as e " +
            "JOIN e.category as c " +
            "LEFT JOIN Request as r ON e.id = r.event.id " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR c.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate >= CURRENT_TIMESTAMP) " +
            "GROUP BY e.id " +
            "HAVING (:onlyAvailable = false OR COUNT(r) < e.participantLimit)")
    List<Event> findPublishedEventsByParametersWithTimeNow(@Param("text") String text,
                                                           @Param("categories") List<Long> categories,
                                                           @Param("paid") Boolean paid,
                                                           @Param("onlyAvailable") Boolean onlyAvailable,
                                                           Pageable pageable);
}
