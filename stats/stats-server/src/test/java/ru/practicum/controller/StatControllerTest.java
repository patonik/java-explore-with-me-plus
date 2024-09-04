package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.dto.ServiceHitDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
class StatControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private StatService statService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void registerHit() throws Exception {
        ServiceHitDto serviceHitDto = ServiceHitDto.builder()
            .app("testApp")
            .uri("/test")
            .ip("127.0.0.1")
            .timestamp(LocalDateTime.now())
            .build();

        Mockito.when(statService.registerHit(any(ServiceHitDto.class)))
            .thenReturn(serviceHitDto);

        mockMvc.perform(post("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(serviceHitDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.app").value("testApp"))
            .andExpect(jsonPath("$.uri").value("/test"))
            .andExpect(jsonPath("$.ip").value("127.0.0.1"));
    }

    @Test
    void getStats() throws Exception {
        HitListElementDto hitListElementDto = HitListElementDto.builder()
            .app("testApp")
            .uri("/test")
            .hits(10L)
            .build();

        List<HitListElementDto> hitListElementDtos = Collections.singletonList(hitListElementDto);

        Mockito.when(statService.getHits(any(String.class), any(String.class), any(String[].class), anyBoolean()))
            .thenReturn(hitListElementDtos);

        mockMvc.perform(get("/stats")
                .param("start", "2023-01-01 00:00:00")
                .param("end", "2023-01-02 00:00:00")
                .param("uris", "/test")
                .param("unique", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].app").value("testApp"))
            .andExpect(jsonPath("$[0].uri").value("/test"))
            .andExpect(jsonPath("$[0].hits").value(10));
    }
}