package ru.practicum.priv.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.priv.service.PrivateRequestServiceImpl;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final PrivateRequestServiceImpl requestService;

    @GetMapping
    public ResponseEntity<ParticipationRequestDto> getMyRequests(@PathVariable Long userId) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addMyRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.atInfo()
                .addArgument(userId)
                .addArgument(eventId)
                .log("Received request to add participation request with userId: {} and eventId: {}");
        return new ResponseEntity<>(requestService.addMyRequest(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelMyRequest(@PathVariable Long userId,
                                                                   @PathVariable Long requestId) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
