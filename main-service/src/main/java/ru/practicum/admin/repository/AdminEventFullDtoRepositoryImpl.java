package ru.practicum.admin.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.request.Status;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminEventFullDtoRepositoryImpl implements AdminEventFullDtoRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<EventFullDto> getEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Pageable pageable) {
        Object[] params = new Object[] {users, states, categories, rangeStart, rangeEnd};
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<EventFullDto> criteriaQuery = criteriaBuilder.createQuery(EventFullDto.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<Request> subRoot = subquery.from(Request.class);
        subquery.select(criteriaBuilder.count(subRoot.get("id")));
        subquery.where(
            criteriaBuilder.equal(subRoot.get("event").get("id"), root.get("id")),
            criteriaBuilder.equal(subRoot.get("status"), Status.CONFIRMED)
        );

        List<Predicate> predicates = getPredicates(criteriaBuilder, root, params);
        criteriaQuery.select(criteriaBuilder.construct(EventFullDto.class,
            root.get("id"),
            root.get("annotation"),
            criteriaBuilder.construct(CategoryDto.class, root.get("category").get("id"),
                root.get("category").get("name")),
            root.get("createdOn"),
            root.get("publishedOn"),
            root.get("description"),
            root.get("eventDate"),
            criteriaBuilder.construct(UserShortDto.class, root.get("initiator").get("id"),
                root.get("initiator").get("name")),
            root.get("location"),
            root.get("paid"),
            root.get("participantLimit"),
            root.get("requestModeration"),
            root.get("title"),
            root.get("state"),
            subquery.getSelection(),
            criteriaBuilder.nullLiteral(Long.class))
        );
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        int pageSize = pageable.getPageSize();
        TypedQuery<EventFullDto> eventFullDtoTypedQuery =
            em.createQuery(criteriaQuery).setFirstResult(pageable.getPageNumber() * pageSize)
                .setMaxResults(pageSize);
        return eventFullDtoTypedQuery.getResultList();
    }

    private List<Predicate> getPredicates(CriteriaBuilder criteriaBuilder, Root<Event> root, Object... args) {
        List<Predicate> predicates = new ArrayList<>();
        Long[] userIds = (Long[]) args[0];
        if (userIds != null && userIds.length > 0) {
            predicates.add(root.get("initiator").get("id").in(Arrays.asList(userIds)));
        }
        String[] states = (String[]) args[1];
        if (states != null && states.length > 0) {
            predicates.add(root.get("state").in(Arrays.asList(states)));
        }
        Long[] categories = (Long[]) args[2];
        if (categories != null && categories.length > 0) {
            predicates.add(root.get("category").get("id").in(Arrays.asList(categories)));
        }
        LocalDateTime rangeStart = (LocalDateTime) args[3];
        LocalDateTime rangeEnd = (LocalDateTime) args[4];
        if (rangeStart != null && rangeEnd != null) {
            predicates.add(criteriaBuilder.between(root.get("eventDate"), rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            predicates.add(criteriaBuilder.between(root.get("eventDate"), rangeStart, LocalDateTime.now()));
        } else {
            predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }
        return predicates;
    }
}
