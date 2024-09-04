package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.configs.CommonConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = CommonConfig.class)
public class HttpStatsServerImplTest {

    @MockBean
    private HttpStatsServer httpStatsServer;

    @Test
    void getStats_whenCalled_thenReturnsData() {
        var start = "2023-01-01 00:00:00";
        var end = "2023-12-31 23:59:59";
        var uris = List.of("/events/1", "/events/2");
        boolean unique = true;

        var expectedResponse = """
            [
                {
                    "app": "ewm-main-service",
                    "uri": "/events/1",
                    "hits": 6
                }
            ]
        """;

        when(httpStatsServer.getStats(eq(start), eq(end), eq(uris), eq(unique), eq(String.class)))
                .thenReturn(expectedResponse);

        var response = httpStatsServer.getStats(start, end, uris, unique, String.class);
        assertEquals(expectedResponse, response);
    }

    @Test
    void sendHit_whenCalled_thenReturnsResponse() {
        var hit = """
            {
                "app": "ewm-main-service",
                "uri": "/events/1",
                "ip": "192.163.0.1",
                "timestamp": "2022-09-06 11:00:23"
            }
        """;
        var expectedResponse = """
            {
                "response": "hit received"
            }
        """;

        when(httpStatsServer.sendHit(eq(hit), eq(String.class)))
                .thenReturn(expectedResponse);

        var response = httpStatsServer.sendHit(hit, String.class);
        assertEquals(expectedResponse, response);
    }
}