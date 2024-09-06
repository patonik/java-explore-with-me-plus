package ru.practicum.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.HttpStatsClient;
import ru.practicum.HttpStatsClientImpl;
import ru.practicum.HttpStatsClientLoggingDecorator;

@Configuration
public class CommonConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpStatsClient httpStatsServer(RestTemplate restTemplate) {
        var httpStatsServer = new HttpStatsClientImpl(restTemplate);
        return new HttpStatsClientLoggingDecorator(httpStatsServer);
    }
}