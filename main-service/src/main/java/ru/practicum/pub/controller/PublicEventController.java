package ru.practicum.pub.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.DataTransferConvention;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.SortCriterium;

import java.time.LocalDateTime;

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
public class PublicEventController {
    @GetMapping
    public ResponseEntity<EventShortDto> getEvents(@RequestParam String text,
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
                                                   Integer size) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
