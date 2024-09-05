package ru.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "HIT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HIT_ID_SEQ")
    @SequenceGenerator(name = "HIT_ID_SEQ", sequenceName = "HIT_ID_SEQ", allocationSize = 1)
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceHit that = (ServiceHit) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ServiceHit{" +
            "id=" + id +
            ", app='" + app + '\'' +
            ", uri='" + uri + '\'' +
            ", ip='" + ip + '\'' +
            ", created=" + created +
            '}';
    }
}
