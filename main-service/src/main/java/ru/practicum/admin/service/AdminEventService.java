package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.repository.AdminCategoryRepository;
import ru.practicum.admin.repository.AdminEventRepository;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventAdminRequestMapper;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminCategoryRepository categoryRepository;
    private final UpdateEventAdminRequestMapper updateEventAdminRequestMapper;
    private final EventFullDtoMapper eventFullDtoMapper;

    public List<EventFullDto> getEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return adminEventRepository.getEvents(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event =
            adminEventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        Long categoryId = adminRequest.getCategoryId();
        Category category;
        if (categoryId != null) {
            category =
                categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        } else {
            category = event.getCategory();
        }
        event = updateEventAdminRequestMapper.updateEvent(adminRequest, event,
            category);
        event = adminEventRepository.save(event);
        RequestCount requestCount = adminEventRepository.getRequestCountByEventAndStatus(eventId, Status.CONFIRMED);
        return eventFullDtoMapper.toDto(event, requestCount.getConfirmedRequests());
    }
}
