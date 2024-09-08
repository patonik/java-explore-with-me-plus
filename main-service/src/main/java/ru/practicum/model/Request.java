package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.request.Status;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_ID_SEQ")
    @SequenceGenerator(name = "REQUEST_ID_SEQ", sequenceName = "REQUEST_ID_SEQ", allocationSize = 1)
    Long id;

    @ManyToOne(targetEntity = Event.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID", referencedColumnName = "ID", foreignKey = @ForeignKey(name = "FK_REQUEST_EVENT"))
    Event event;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_REQUEST_REQUESTER"))
    User requester;

    @Column(name = "REQUEST_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "CREATED", nullable = false)
    LocalDateTime created;
}
