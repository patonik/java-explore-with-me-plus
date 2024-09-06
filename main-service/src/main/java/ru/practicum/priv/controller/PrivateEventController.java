package ru.practicum.priv.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.dto.event.UpdateEventUserRequest;

@RestController
@RequestMapping("users/{userId}/events")
public class PrivateEventController {
    @GetMapping
    public ResponseEntity<EventShortDto> getMyEvents(@PathVariable("userId") String userId,
                                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(@PathVariable("userId") String userId,
                                                 @RequestBody NewEventDto newEventDto) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getMyEvent(@PathVariable Long userId,
                                                   @PathVariable Long eventId) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateMyEvent(@PathVariable Long userId,
                                                      @PathVariable Long eventId,
                                                      @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<ParticipationRequestDto> getMyEventRequests(@PathVariable Long userId,
                                                                      @PathVariable Long eventId) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateMyEventRequests(@PathVariable Long userId,
                                                                                @PathVariable Long eventId,
                                                                                @RequestBody
                                                                                EventRequestStatusUpdateRequest updateRequest) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
