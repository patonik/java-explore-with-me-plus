package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class HttpStatsClientImpl implements HttpStatsClient {

    public static final String URL = "http://localhost:9090";

    private final RestTemplate restTemplate;

    @Override
    public <R> Optional<R> getStats(String start, String end, List<String> uris, boolean unique, Class<R> responseType) {
        var uriBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique)
                .queryParam("uris", uris);

        var fullUrl = uriBuilder.build().toUri();
        return Optional.ofNullable(restTemplate.getForObject(fullUrl, responseType));
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        var uriBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .path("/hit");
        var fullUrl = uriBuilder.build().toUri();
        return Optional.ofNullable(restTemplate.postForObject(fullUrl, hit, responseType));
    }
}