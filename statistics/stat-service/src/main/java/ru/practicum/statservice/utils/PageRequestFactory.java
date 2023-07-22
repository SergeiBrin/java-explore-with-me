package ru.practicum.statservice.utils;

import org.springframework.data.domain.PageRequest;

public class PageRequestFactory {
    public static PageRequest createPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}
