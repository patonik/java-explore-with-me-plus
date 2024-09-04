package ru.practicum.configs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CommonConfig.class)
public class CommonConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void httpStatsServer_whenContextLoads_thenBeanShouldNotBeNull() {
        var httpStatsServer = context.getBean("httpStatsServer");
        assertNotNull(httpStatsServer);
    }

    @Test
    void restTemplate_whenContextLoads_thenBeanShouldNotBeNull() {
        var restTemplate = context.getBean("restTemplate");
        assertNotNull(restTemplate);
    }
}