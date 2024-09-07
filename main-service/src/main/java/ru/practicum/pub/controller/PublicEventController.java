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
