package ru.practicum.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.State;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVENT_ID_SEQ")
    @SequenceGenerator(name = "EVENT_ID_SEQ", sequenceName = "EVENT_ID_SEQ", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String annotation;
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")
    private Category category;
    @CreationTimestamp
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private User initiator;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "lat", column = @Column(name = "LOCATION_LAT", nullable = false)),
        @AttributeOverride(name = "lon", column = @Column(name = "LOCATION_LON", nullable = false))
    })
    private Location location;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    private Integer participantLimit;
    @Column(nullable = false)
    private Boolean requestModeration;
    @Column(nullable = false)
    private String title;
    @Column(name = "EVENT_STATE", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + id +
            ", annotation='" + annotation + '\'' +
            ", createdOn=" + createdOn +
            ", publishedOn=" + publishedOn +
            ", description='" + description + '\'' +
            ", eventDate=" + eventDate +
            ", paid=" + paid +
            ", participantLimit=" + participantLimit +
            ", requestModeration=" + requestModeration +
            ", title='" + title + '\'' +
            ", state=" + state +
            ", location=" + location +
            '}';
    }
}
