package ru.practicum;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpStatsClientLoggingDecorator implements HttpStatsClient {

    private final HttpStatsClient delegate;

    public HttpStatsClientLoggingDecorator(HttpStatsClient delegate) {
        this.delegate = delegate;
    }

    @Override
    public <R> Optional<R> getStats(String start, String end, List<String> uris, boolean unique, Class<R> responseType) {
        log.info("Getting stats from {} to {} for URIs: {}", start, end, uris.toString());
        Optional<R> result = delegate.getStats(start, end, uris, unique, responseType);
        if (result.isPresent()) {
            log.info("Stats received: {}", result.get());
        } else {
            log.info("Stats received: no stats available");
        }
        return result;
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        log.info("Sending hit: {}", hit);
        Optional<R> result = delegate.sendHit(hit, responseType);
        if (result.isPresent()) {
            log.info("Hit response: {}", result.get());
        } else {
            log.info("Hit response: no response received");
        }
        return result;
    }
}