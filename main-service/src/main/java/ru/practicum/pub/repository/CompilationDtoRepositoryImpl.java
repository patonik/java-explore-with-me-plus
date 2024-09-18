package ru.practicum.pub.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.request.Status;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.Request;

import java.util.List;

public class CompilationDtoRepositoryImpl implements CompilationDtoRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CompilationDto> findAllCompilationDtos(Boolean pinned, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CompilationDto> cq = cb.createQuery(CompilationDto.class);
        Root<Compilation> root = cq.from(Compilation.class);
        SetJoin<Compilation, Event> eventJoin = root.joinSet("events", JoinType.LEFT);

        // Subquery to count confirmed requests
        Subquery<Long> confirmedRequestsSubquery = cq.subquery(Long.class);
        Root<Request> requestRoot = confirmedRequestsSubquery.from(Request.class);
        confirmedRequestsSubquery.select(cb.count(requestRoot.get("id")));
        confirmedRequestsSubquery.where(
                cb.equal(requestRoot.get("event").get("id"), eventJoin.get("id")),
                cb.equal(requestRoot.get("status"), Status.CONFIRMED)
        );

        // Select CompilationDto with nested EventShortDto
        cq.select(cb.construct(
                CompilationDto.class,
                root.get("id"),
                cb.construct(
                        EventShortDto.class,
                        eventJoin.get("id"),
                        eventJoin.get("annotation"),
                        cb.construct(
                                CategoryDto.class,
                                eventJoin.get("category").get("id"),
                                eventJoin.get("category").get("name")
                        ),
                        confirmedRequestsSubquery.getSelection(),
                        eventJoin.get("eventDate"),
                        eventJoin.get("createdOn"),
                        cb.construct(
                                UserShortDto.class,
                                eventJoin.get("initiator").get("id"),
                                eventJoin.get("initiator").get("name")
                        ),
                        eventJoin.get("paid"),
                        eventJoin.get("title"),
                        cb.nullLiteral(Long.class) // views will be filled later
                ),
                root.get("pinned"),
                root.get("title")
        ));
        if (pinned != null) {
            cq.where(cb.equal(root.get("pinned"), pinned));
        }
        TypedQuery<CompilationDto> query = em.createQuery(cq);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}
