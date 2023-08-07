package ru.practicum.mainservice.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestFactory {
    public static PageRequest buildPageRequestWithSort(int from, int size, Sort sort) {
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }

    public static PageRequest buildPageRequestWithoutSort(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }
}
