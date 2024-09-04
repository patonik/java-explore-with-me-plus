package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.dto.ServiceHitDto;
import ru.practicum.service.StatService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<ServiceHitDto> registerHit(@RequestBody @Valid ServiceHitDto serviceHitDto) {
        return new ResponseEntity<>(statService.registerHit(serviceHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<HitListElementDto>> getStats(
        @RequestParam("start") String start,
        @RequestParam("end") String end,
        @RequestParam("uris") String[] uris,
        @RequestParam(value = "unique", defaultValue = "false")
        Boolean unique) {
        return new ResponseEntity<>(statService.getHits(start, end, uris, unique), HttpStatus.OK);
    }
}
