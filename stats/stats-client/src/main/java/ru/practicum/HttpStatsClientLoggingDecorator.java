package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpStatsClientLoggingDecorator implements HttpStatsClient {

    private final HttpStatsClient delegate;

    public HttpStatsClientLoggingDecorator(HttpStatsClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public <R> Optional<R> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    boolean unique,
                                    Class<R> responseType) {
        log.info("Getting stats from {} to {} for URIs: {}", start, end, uris.toString());
        try {
            var optResult = delegate.getStats(start, end, uris, unique, responseType);
            if (optResult.isPresent()) {
                log.info("Stats received: {}", optResult.get());
            } else {
                log.info("Stats received: no stats available");
            }
            return optResult;
        } catch (RestClientException e) {
            log.error("Error during fetching stats: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        log.info("Sending hit: {}", hit);
        try {
            var optResult = delegate.sendHit(hit, responseType);
            if (optResult.isPresent()) {
                log.info("Hit response: {}", optResult.get());
            } else {
                log.info("Hit response: no response received");
            }
            return optResult;
        } catch (RestClientException e) {
            log.error("Error during sending hit: {}", e.getMessage());
            return Optional.empty();
        }
    }
}