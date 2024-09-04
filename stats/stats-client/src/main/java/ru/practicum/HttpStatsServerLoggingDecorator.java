package ru.practicum;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HttpStatsServerLoggingDecorator implements HttpStatsServer {

    private final HttpStatsServer delegate;

    public HttpStatsServerLoggingDecorator(HttpStatsServer delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T getStats(String start, String end, List<String> uris, boolean unique, Class<T> responseType) {
        log.info("Getting stats from {} to {} for URIs: {}", start, end, uris.toString());
        T result = delegate.getStats(start, end, uris, unique, responseType);
        log.info("Stats received: {}", result);
        return result;
    }

    @Override
    public <T, R> R sendHit(T hit, Class<R> responseType) {
        log.info("Sending hit: {}", hit);
        R result = delegate.sendHit(hit, responseType);
        log.info("Hit response: {}", result);
        return result;
    }
}