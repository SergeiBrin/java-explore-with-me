package ru.practicum.statclient.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.statdto.dto.EndpointHitDto;
import ru.practicum.statdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class HttpClient {
    private final RestTemplate restTemplate;
    private static final String GET_URI = "http://localhost:9090/stats";
    private static final String POST_URI = "http://localhost:9090/hit";

    public ResponseEntity<List<ViewStatsDto>> getStatistics(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique) {

        // блок создания данных для запроса
        String uriTemplate = buildUrl();
        Map<String, Object> params = buildParams(start, end, uris, unique);
        HttpEntity<Object> entity = buildHttpEntity();
        ParameterizedTypeReference<List<ViewStatsDto>> responseType = buildParameterizedType();

        ResponseEntity<List<ViewStatsDto>> response =
                restTemplate.exchange(uriTemplate, HttpMethod.GET, entity, responseType, params);

        log.info("");

        return response;
    }
    public ResponseEntity<Void> createHit(EndpointHitDto endpointHitDto) {
        ResponseEntity<Void> status = restTemplate.postForEntity(POST_URI, endpointHitDto, Void.class);
        log.info("");

        return new ResponseEntity<>(status.getStatusCode());
    }

    private String buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(GET_URI)
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("uris", "{uris}")
                .queryParam("unique", "{unique}")
                .encode()
                .toUriString();
    }

    private Map<String, Object> buildParams(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startFormat = start.format(formatter);
        String endFormat = end.format(formatter);

        Map<String, Object> params = new HashMap<>();
        params.put("start", startFormat);
        params.put("end", endFormat);
        params.put("uris", uris);
        params.put("unique", unique);

        return params;
    }

    private HttpEntity<Object> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity<>(headers);
    }

    private ParameterizedTypeReference<List<ViewStatsDto>> buildParameterizedType() {

        return new ParameterizedTypeReference<>() {};
    }
}
