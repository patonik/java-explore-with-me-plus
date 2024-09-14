package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.admin.service.AdminEventService;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
 * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
 */
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam Long[] users,
                                                        @RequestParam String[] states,
                                                        @RequestParam Long[] categories,
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime rangeStart,
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                        LocalDateTime rangeEnd,
                                                        @RequestParam(required = false, defaultValue = "0")
                                                        Integer from,
                                                        @RequestParam(required = false, defaultValue = "10")
                                                        Integer size) {
        return new ResponseEntity<>(
            adminEventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long eventId,
                                                    @RequestBody UpdateEventAdminRequest adminRequest) {
        return new ResponseEntity<>(adminEventService.updateEvent(eventId, adminRequest), HttpStatus.OK);
    }
}
