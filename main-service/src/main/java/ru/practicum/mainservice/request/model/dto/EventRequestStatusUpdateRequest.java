package ru.practicum.mainservice.request.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.mainservice.request.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    @NotNull
    private RequestStatus status;
}
