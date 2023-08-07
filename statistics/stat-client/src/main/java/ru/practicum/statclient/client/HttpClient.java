package ru.practicum.statclient.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.statdto.dto.EndpointHitDto;
import ru.practicum.statdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HttpClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private static final String GET_URI = "/stats";
    private static final String POST_URI = "/hit";

    public HttpClient(RestTemplateBuilder restTemplateBuilder, Environment env) {
        this.restTemplate = restTemplateBuilder.build();
        baseUrl = env.getProperty("statistics.server.url");
    }

    public List<ViewStatsDto> getStatistics(
            LocalDateTime start,
            LocalDateTime end,
            String uri,
            Boolean unique) {

        // блок создания данных для запроса
        String uriTemplate = buildUrl();
        Map<String, Object> params = buildParams(start, end, uri, unique);
        HttpEntity<Object> entity = buildHttpEntity();
        ParameterizedTypeReference<List<ViewStatsDto>> responseType = buildParameterizedType();
        ResponseEntity<List<ViewStatsDto>> response =
                restTemplate.exchange(uriTemplate, HttpMethod.GET, entity, responseType, params);
        log.info("GET запрос в сервис статистики обработан успешно. response={}", response);

        return response.getBody();
    }

    public ResponseEntity<Void> createHit(EndpointHitDto endpointHitDto) {
        ResponseEntity<Void> status = restTemplate.postForEntity(baseUrl + POST_URI, endpointHitDto, Void.class);
        log.info("POST запрос в сервис статистики обработан успешно");

        return new ResponseEntity<>(status.getStatusCode());
    }

    private String buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(baseUrl + GET_URI)
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("uris", "{uris}")
                .queryParam("unique", "{unique}")
                .encode()
                .toUriString();
    }

    private Map<String, Object> buildParams(LocalDateTime start, LocalDateTime end, String uri, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startFormat = start.format(formatter);
        String endFormat = end.format(formatter);

        Map<String, Object> params = new HashMap<>();
        params.put("start", startFormat);
        params.put("end", endFormat);
        params.put("uris", uri);
        params.put("unique", unique);

        return params;
    }

    private HttpEntity<Object> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity<>(headers);
    }

    // Без этого типа не будут возвращаться коллекции с объектами.
    private ParameterizedTypeReference<List<ViewStatsDto>> buildParameterizedType() {
        return new ParameterizedTypeReference<>() {};
    }
}
