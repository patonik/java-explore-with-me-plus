package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.configs.CommonConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = CommonConfig.class)
public class HttpStatsClientImplTest {

    @MockBean
    private HttpStatsClient httpStatsClient;

    @Test
    void getStats_Optional_whenCalled_thenReturnsData() {
        var params = StatsParameters.builder()
                .start("2023-01-01 00:00:00")
                .end("2023-12-31 23:59:59")
                .uris(List.of("/events/1", "/events/2"))
                .unique(true)
                .build();
        var expectedResponse = String.format("""
                    [
                        %1$s
                            "app": "ewm-main-service",
                            "uri": "/events/1",
                            "hits": 6
                        %2$s
                    ]
                """, "{", "}");

        when(httpStatsClient.getStats(params))
                .thenReturn(Optional.of(expectedResponse));

        var response = httpStatsClient.getStats(params);
        assertFalse(response.isEmpty());
        assertEquals(expectedResponse, response.get());
    }

    @Test
    void sendHit_Optional_whenCalled_thenReturnsResponse() {
        var hit = String.format("""
                    %1$s
                        "app": "ewm-main-service",
                        "uri": "/events/1",
                        "ip": "192.163.0.1",
                        "timestamp": "2022-09-06 11:00:23"
                    %2$s
                """, "{", "}");
        var expectedResponse = String.format("""
                    %1$s
                        "response": "hit received"
                    %2$s
                """, "{", "}");

        when(httpStatsClient.sendHit(eq(hit), eq(String.class)))
                .thenReturn(Optional.of(expectedResponse));

        var response = httpStatsClient.sendHit(hit, String.class);
        assertFalse(response.isEmpty());
        assertEquals(expectedResponse, response.get());
    }
}