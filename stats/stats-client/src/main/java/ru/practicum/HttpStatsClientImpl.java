package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.StatResponseDto;

import java.util.List;
import java.util.Optional;

import static ru.practicum.constants.DataTransferConvention.HIT_PATH;
import static ru.practicum.constants.DataTransferConvention.STATS_PATH;
import static ru.practicum.constants.DataTransferConvention.STAT_SERVICE_URL;

@RequiredArgsConstructor
public class HttpStatsClientImpl implements HttpStatsClient {

    private final RestTemplate restTemplate;

    public List<StatResponseDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        String url = UriComponentsBuilder
            .fromHttpUrl(STAT_SERVICE_URL)
            .path(STATS_PATH)
            .queryParam("start", start)
            .queryParam("end", end)
            .queryParam("uris", String.join(",", uris))
            .queryParam("unique", unique)
            .build()
            .toUriString();

        ResponseEntity<List<StatResponseDto>> responseEntity =
            restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        return responseEntity.getBody();
    }


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