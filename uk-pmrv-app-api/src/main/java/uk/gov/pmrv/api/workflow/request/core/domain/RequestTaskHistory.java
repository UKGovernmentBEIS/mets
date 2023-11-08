package uk.gov.pmrv.api.workflow.request.core.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_task_history")
public class RequestTaskHistory {
    @Id
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "process_task_id", unique = true)
    private String processTaskId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestTaskType type;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "pause_date")
    private LocalDate pauseDate;

    @Basic(fetch = FetchType.LAZY)
    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private RequestTaskPayload payload;

    @Column(name = "end_date")
    private LocalDateTime endDate;


}
