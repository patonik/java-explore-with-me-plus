package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static ru.practicum.constants.DataTransferConvention.*;

@RequiredArgsConstructor
public class HttpStatsClientImpl implements HttpStatsClient {

    private final RestTemplate restTemplate;

    @Override
    public <R> Optional<List<R>> getStats(StatsParameters<R> param) {
        R[] responseArray = restTemplate.getForObject(
                UriComponentsBuilder.fromHttpUrl(STAT_SERVICE_URL)
                        .path(STATS_PATH)
                        .queryParam("start", param.getStart())
                        .queryParam("end", param.getEnd())
                        .queryParam("uris", String.join(",", param.getUris()))
                        .queryParam("unique", param.isUnique())
                        .build().toUri(), param.getResponseType());

        return Optional.ofNullable(responseArray != null ? List.of(responseArray) : null);
    }

    @Override
    public <T, R> Optional<R> sendHit(T hit, Class<R> responseType) {
        return Optional.ofNullable(restTemplate.postForObject(UriComponentsBuilder.fromHttpUrl(STAT_SERVICE_URL)
                .path(HIT_PATH)
                .build().toUri(), hit, responseType));
    }
}