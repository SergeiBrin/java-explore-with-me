package ru.practicum.mainservice.exception.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ApiError {
    private List<StackTraceElement> errors; // Список стектрейсов или описания ошибок

    private String message; // Сообщение об ошибке

    private String reason; // Общее описание причины ошибки

    private HttpStatus status; // Код статуса HTTP-ответа

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}
