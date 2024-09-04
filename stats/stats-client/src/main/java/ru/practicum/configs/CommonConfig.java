package ru.practicum.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.HttpStatsServer;
import ru.practicum.HttpStatsServerImpl;
import ru.practicum.HttpStatsServerLoggingDecorator;

@Configuration
public class CommonConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpStatsServer httpStatsServer(RestTemplate restTemplate) {
        var httpStatsServer = new HttpStatsServerImpl(restTemplate);
        return new HttpStatsServerLoggingDecorator(httpStatsServer);
    }
}