package ru.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "COMPILATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMP_ID_SEQ")
    @SequenceGenerator(name = "COMP_ID_SEQ", sequenceName = "COMP_ID_SEQ", allocationSize = 1)
    private Long id;
    private Boolean pinned;
    private String title;
    @OneToMany(targetEntity = Event.class)
    @JoinColumn(name = "EVENT_ID", referencedColumnName = "ID")
    private Set<Event> events = new HashSet<>();

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compilation that = (Compilation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return "Compilation{" +
            "id=" + id +
            ", pinned=" + pinned +
            ", title='" + title + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
