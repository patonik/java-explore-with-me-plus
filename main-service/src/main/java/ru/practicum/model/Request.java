package ru.practicum.model;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.dto.event.request.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQ_ID_SEQ")
    @SequenceGenerator(name = "REQ_ID_SEQ", sequenceName = "REQ_ID_SEQ", allocationSize = 1)
    private Long id;
    @CreationTimestamp
    private LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private User requester;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Event.class)
    @JoinColumn(name = "EVENT_ID", referencedColumnName = "ID", nullable = false)
    private Event event;
    @Enumerated(EnumType.STRING)
    private Status status;
}
