package ru.practicum;

import java.util.Optional;

public interface HttpStatsClient {

    <R> Optional<R> getStats(StatsParameters<R> params);

    <T, R> Optional<R> sendHit(T hitDto, Class<R> responseType);
}