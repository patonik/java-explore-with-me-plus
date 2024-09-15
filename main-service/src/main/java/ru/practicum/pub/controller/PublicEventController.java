package ru.practicum.pub.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.DataTransferConvention;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.StatRequestDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.SortCriterium;
import ru.practicum.pub.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Обратите внимание:
 * <p>
 *     <ul>
 *         <li>это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события</li>
 *         <li>текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв</li>
 *         <li>если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события,
 *             которые произойдут позже текущей даты и времени</li>
 *         <li>информация о каждом событии должна включать в себя количество просмотров
 *             и количество уже одобренных заявок на участие</li>
 *         <li>информацию о том, что по этому эндпоинту был осуществлен и обработан запрос,
 *             нужно сохранить в сервисе статистики</li>
 *     </ul>
 * </p>
 * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final PublicEventService publicEventService;
    private final HttpStatsClient httpStatsClient;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@RequestParam String text,
                                                         @RequestParam Long[] categories,
                                                         @RequestParam Boolean paid,
                                                         @RequestParam
                                                         @DateTimeFormat(pattern = DataTransferConvention.DATE_TIME_PATTERN)
                                                         LocalDateTime rangeStart,
                                                         @RequestParam
                                                         @DateTimeFormat(pattern = DataTransferConvention.DATE_TIME_PATTERN)
                                                         LocalDateTime rangeEnd,
                                                         @RequestParam(required = false, defaultValue = "false")
                                                         Boolean onlyAvailable,
                                                         @RequestParam SortCriterium sort,
                                                         @RequestParam(required = false,
                                                             defaultValue = DataTransferConvention.FROM)
                                                         Integer from,
                                                         @RequestParam(required = false,
                                                             defaultValue = DataTransferConvention.SIZE)
                                                         Integer size, HttpServletRequest req) {
        sendHitToStatsService(req);
        return new ResponseEntity<>(
            publicEventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size),
            HttpStatus.OK);
    }

    /**
     * Обратите внимание:
     * событие должно быть опубликовано
     * информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если события с заданным id не найдено, возвращает статус код 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id, HttpServletRequest request) {
        sendHitToStatsService(request);
        return new ResponseEntity<>(publicEventService.getEvent(id), HttpStatus.OK);
    }

    private void sendHitToStatsService(HttpServletRequest request) {
        StatRequestDto hit = StatRequestDto.builder()
            .app("public-event-service")
            .uri(request.getRequestURI())
            .ip(request.getRemoteAddr())
            .timestamp(LocalDateTime.parse(
                LocalDateTime.now().format(DataTransferConvention.DATE_TIME_FORMATTER))) // Current timestamp
            .build();

        // Send the hit to the statistics service
        httpStatsClient.sendHit(hit, StatRequestDto.class);
    }


}
