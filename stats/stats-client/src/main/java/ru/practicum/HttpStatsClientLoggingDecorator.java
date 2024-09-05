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
        R result = delegate.getStats(start, end, uris, unique, responseType);
        log.info("Stats received: {}", result);
        return result;
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        log.info("Sending hit: {}", hit);
        R result = delegate.sendHit(hit, responseType);
        log.info("Hit response: {}", result);
        return result;
    }
}