package ru.practicum;

import java.util.List;
import java.util.Optional;

public interface HttpStatsClient {

    <R> Optional<List<R>> getStats(StatsParameters<R> params);

    <T, R> Optional<R> sendHit(T hitDto, Class<R> responseType);
}