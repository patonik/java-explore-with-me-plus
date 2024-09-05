package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.model.ServiceHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<ServiceHit, Long>, HitListElementRepository {
    @Query("""
        select new ru.practicum.dto.HitListElementDto(s.app, s.uri, count(distinct s.ip))
        from ServiceHit s
        where s.created between :start and :end and s.uri in (:uris)
        group by s.app, s.uri
        order by s.app, s.uri asc""")
    List<HitListElementDto> getHitListElementDtosDistinctIp(LocalDateTime start,
                                                            LocalDateTime end,
                                                            String[] uris);

    @Query("""
        select new ru.practicum.dto.HitListElementDto(s.app, s.uri, count(s.ip))
        from ServiceHit s
        where s.created between :start and :end
        and s.uri in (:uris)
        group by s.app, s.uri
        order by s.app, s.uri asc""")
    List<HitListElementDto> getHitListElementDtos(LocalDateTime start,
                                                  LocalDateTime end,
                                                  String[] uris);
}
