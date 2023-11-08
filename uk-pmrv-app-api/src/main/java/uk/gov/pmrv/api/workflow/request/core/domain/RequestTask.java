package uk.gov.pmrv.api.workflow.request.core.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PreRemove;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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

/**
 * The request task entity that represents a workflow task.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_task")
@NamedQuery(
    name = RequestTask.NAMED_QUERY_FIND_REQUEST_TASK_BY_ID,
    query = "select rt "
        + "from RequestTask rt "
        + "where rt.id = :id"
)
@NamedEntityGraph(
    name = RequestTask.NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST,
    attributeNodes = {
        @NamedAttributeNode("request")
    }
)
public class RequestTask {

    public static final String NAMED_QUERY_FIND_REQUEST_TASK_BY_ID = "RequestTask.findRequestTaskById";
    public static final String NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST = "RequestTask.request-task-request-graph";

    @Id
    @SequenceGenerator(name = "request_task_id_generator", sequenceName = "request_task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_task_id_generator")
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

    @Version
    @Setter(AccessLevel.NONE)
    private long version;

    @PreRemove
    public void removeFromRequest() {
        this.getRequest().removeRequestTask(this);
    }
}
