package ru.practicum;

import java.util.List;

public interface HttpStatsServer {

    <T> T getStats(String start, String end, List<String> uris, boolean unique, Class<T> responseType);

    <T, R> R sendHit(T hit, Class<R> responseType);
}