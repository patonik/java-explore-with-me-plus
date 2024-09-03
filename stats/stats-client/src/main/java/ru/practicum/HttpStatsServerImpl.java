package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HttpStatsServerImpl implements HttpStatsServer {

    public static final String URL = "http://localhost:9090";

    private final RestTemplate restTemplate;

    @Override
    public <T> T getStats(String start, String end, List<String> uris, boolean unique, Class<T> responseType) {
        var uriBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique)
                .queryParam("uris", uris);

        var fullUrl = uriBuilder.build().toUri();
        return restTemplate.getForObject(fullUrl, responseType);
    }

    @Override
    public <T, R> R sendHit(T hit, Class<R> responseType) {
        var uriBuilder = UriComponentsBuilder.fromHttpUrl(URL)
                .path("/hit");
        var fullUrl = uriBuilder.build().toUri();
        return restTemplate.postForObject(fullUrl, hit, responseType);
    }

}