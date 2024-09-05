package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class HttpStatsClientImpl implements HttpStatsClient {

    public static final String URL = "http://localhost:9090";

    public static final String STATS_PATH = "/stats";

    public static final String HIT_PATH = "/hit";

    private final RestTemplate restTemplate;

    @Override
    public <R> Optional<R> getStats(String start, String end, List<String> uris, boolean unique, Class<R> responseType) {
        return Optional.ofNullable(restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(URL)
                .path(STATS_PATH)
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build().toUri(), responseType));
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        return Optional.ofNullable(restTemplate.postForObject(UriComponentsBuilder.fromHttpUrl(URL)
                .path(HIT_PATH)
                .build().toUri(), hit, responseType));
    }
}