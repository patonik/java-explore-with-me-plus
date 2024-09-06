package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static ru.practicum.constants.DataTransferConvention.*;

@RequiredArgsConstructor
public class HttpStatsClientImpl implements HttpStatsClient {

    private final RestTemplate restTemplate;

    @Override
    public <R> Optional<R> getStats(StatsParameters<R> param) {
        return Optional.ofNullable(restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(STAT_SERVICE_URL)
                .path(STATS_PATH)
                .queryParam("start", param.getStart())
                .queryParam("end", param.getEnd())
                .queryParam("uris", param.getUris())
                .queryParam("unique", param.isUnique())
                .build().toUri(), param.getResponseType()));
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        return Optional.ofNullable(restTemplate.postForObject(UriComponentsBuilder.fromHttpUrl(STAT_SERVICE_URL)
                .path(HIT_PATH)
                .build().toUri(), hit, responseType));
    }
}