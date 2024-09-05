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
    public <R> Optional<R> getStatsOptional(String start,
                                            String end,
                                            List<String> uris,
                                            boolean unique,
                                            Class<R> responseType) {
        log.info("Getting stats from {} to {} for URIs: {}", start, end, uris.toString());
        try {
            var optResult = delegate.getStatsOptional(start, end, uris, unique, responseType);
            if (optResult.isPresent()) {
                log.info("Stats received: {}", optResult.get());
            } else {
                log.info("Stats received: no stats available");
            }
            return optResult;
        } catch (RestClientException e) {
            log.error("Error during fetching stats: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Deprecated
    public <R> R getStats(String start, String end, List<String> uris, boolean unique, Class<R> responseType) {
        log.info("Getting stats from {} to {} for URIs: {}", start, end, uris.toString());
        R result = delegate.getStats(start, end, uris, unique, responseType);
        log.info("Stats received: {}", result);
        return result;
    }

    @Override
    public <T, R> Optional<R> sendHitOptional(T hit, Class<R> responseType) {
        log.info("Sending hit: {}", hit);
        try {
            var optResult = delegate.sendHitOptional(hit, responseType);
            if (optResult.isPresent()) {
                log.info("Hit response: {}", optResult.get());
            } else {
                log.info("Hit response: no response received");
            }
            return optResult;
        } catch (RestClientException e) {
            log.error("Error during fetching hit: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Deprecated
    public <T, R> R sendHit(T hit, Class<R> responseType) {
        log.info("Sending hit: {}", hit);
        R result = delegate.sendHit(hit, responseType);
        log.info("Hit response: {}", result);
        return result;
    }
}