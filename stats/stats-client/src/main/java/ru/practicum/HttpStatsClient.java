package ru.practicum;

import java.util.List;

public interface HttpStatsClient {

    <R> R getStats(String start, String end, List<String> uris, boolean unique, Class<R> responseType);

    <T, R> R sendHit(T hit, Class<R> responseType);
}