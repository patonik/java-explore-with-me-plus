package ru.practicum.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.model.ServiceHit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HitListElementRepositoryImpl implements HitListElementRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<HitListElementDto> getHitListElementDtos(LocalDateTime start,
                                                         LocalDateTime end,
                                                         Object[] uris,
                                                         boolean unique) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<HitListElementDto> criteriaQuery = criteriaBuilder.createQuery(HitListElementDto.class);
        Root<ServiceHit> root = criteriaQuery.from(ServiceHit.class);
        List<Predicate> predicates = new ArrayList<>();
        Predicate datePredicate = criteriaBuilder.between(root.get("created"), start, end);
        predicates.add(datePredicate);
        if (uris != null && uris.length > 0) {
            Predicate uriPredicate = root.in(uris);
            predicates.add(uriPredicate);
        }
        criteriaQuery.select(criteriaBuilder.construct(
            HitListElementDto.class,
            root.get("app"),
            root.get("uri"),
            unique ? criteriaBuilder.countDistinct(root.get("uri")) : criteriaBuilder.count(root.get("uri"))
        ));
        criteriaQuery.where(predicates.toArray(new Predicate[0]))
            .groupBy(root.get("app"), root.get("uri"))
            .orderBy(criteriaBuilder.asc(root.get("uri")), criteriaBuilder.asc(root.get("uri")));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
