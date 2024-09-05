package ru.practicum;

import java.util.List;
import java.util.Optional;

public interface HttpStatsClient {

    <R> Optional<R> getStatsOptional(String start, String end, List<String> uris, boolean unique, Class<R> responseType);

    <T, R> Optional<R> sendHitOptional(T hit, Class<R> responseType);

    @Deprecated
    <R> R getStats(String start, String end, List<String> uris, boolean unique, Class<R> responseType);

    @Deprecated
    <T, R> R sendHit(T hit, Class<R> responseType);
}