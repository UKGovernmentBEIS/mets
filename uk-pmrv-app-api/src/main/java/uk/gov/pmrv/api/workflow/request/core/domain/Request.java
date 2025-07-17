package uk.gov.pmrv.api.workflow.request.core.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "request",
        indexes = {
                @Index(name = "idx_request_process_instance_id", columnList = "process_instance_id", unique = true)
        })
@NamedQuery(
        name = Request.NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_STATUS_AND_TYPE_NOT_NOTIFICATION,
        query = "select r "
            + "from Request r "
            + "where r.accountId = :accountId "
            + "and r.status = :status "
            + "and r.type <> 'SYSTEM_MESSAGE_NOTIFICATION'"
    )
@NamedNativeQuery(
	    name = Request.NAMED_NATIVE_QUERY_EXIST_BY_TYPE_AND_STATUS_AND_ACCOUNT_ID_AND_METADATA_YEAR,
	    query = "select exists(select 1 "
	        + "from request r "
	        + "where r.type = :type "
	        + "and r.status = :status "
	        + "and r.account_id = :accountId "
	        + "and cast(r.metadata->>'year' as INTEGER) = :year "
	        + ")")
@NamedNativeQuery(
    name = Request.NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_ID_AND_TYPE_IN_AND_METADATA_YEAR,
    resultClass = Request.class,
    query = "select r.* "
        + "from request r "
        + "where r.account_id = :accountId "
        + "and r.type in (:types) "
        + "and cast(r.metadata->>'year' as INTEGER) = :year "
)
@NamedQuery(
    name = Request.NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_TYPE_NOT_IN,
    query = "select new uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO(req.id, req.type) " +
        "from Request req " +
        "where req.accountId = :accountId " +
        "and req.type not in (:requestTypes) " +
        "order by req.creationDate"
)
@NamedNativeQuery(
    name = Request.FIND_BY_ACCOUNT_ID_AND_TYPE_IN_AND_METADATA_YEAR_AND_STATUS_IN_ORDER_BY_END_DATE_DESC,
    resultClass = Request.class,
    query = "select r.* "
        + "from request r "
        + "where r.account_id = :accountId "
        + "and r.type in (:types) "
        + "and r.status in (:statuses)"
        + "and cast(r.metadata->>'year' as INTEGER) = :year "
        + "order by r.end_date desc"
)
public class Request {

    public static final String NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_STATUS_AND_TYPE_NOT_NOTIFICATION = "Request.findRequestsByAccountIdAndStatusAndTypeNotNotification";
    public static final String NAMED_NATIVE_QUERY_EXIST_BY_TYPE_AND_STATUS_AND_ACCOUNT_ID_AND_METADATA_YEAR = "Request.existByTypeAndStatusAndAccountIdAndMetadataYear";
    public static final String NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_TYPE_NOT_IN = "Request.findByAccountIdAndTypeNotIn";
    public static final String NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_ID_AND_TYPE_IN_AND_METADATA_YEAR = "Request.findByAccountIdAndTypeInAndMetadataYear";
    public static final String FIND_BY_ACCOUNT_ID_AND_TYPE_IN_AND_METADATA_YEAR_AND_STATUS_IN_ORDER_BY_END_DATE_DESC = "Request.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc";

    @Id
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @NotNull
    @Column(name = "creation_date")
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    
    /**
     *  Τhe date the first task (user task if exists, otherwise system task) of the request was completed
     */
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @EqualsAndHashCode.Include()
    @Column(name = "process_instance_id", unique = true)
    private String processInstanceId;

    @Column(name = "competent_authority")
    @Enumerated(EnumType.STRING)
    private CompetentAuthorityEnum competentAuthority;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "verification_body_id")
    private Long verificationBodyId;

    @Basic(fetch = FetchType.LAZY)
    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb")
    private RequestPayload payload;

    @Basic(fetch = FetchType.LAZY)
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private RequestMetadata metadata;

    /**
     *  Τhe date the last task of the request was completed
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Builder.Default
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<RequestAction> requestActions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RequestTask> requestTasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RequestTaskHistory> requestTasksHistory = new ArrayList<>();

    public void addRequestAction(RequestAction requestAction) {
        requestAction.setRequest(this);
        this.requestActions.add(requestAction);
    }

    public void addRequestTask(RequestTask requestTask) {
        requestTask.setRequest(this);
        this.requestTasks.add(requestTask);
    }

    public void addRequestTaskHistory(RequestTaskHistory requestTaskHistory) {
        requestTaskHistory.setRequest(this);
        this.requestTasksHistory.add(requestTaskHistory);
    }

    public void removeRequestTask(RequestTask requestTask) {
        this.getRequestTasks().remove(requestTask);
    }


    public Map<String, String> getRequestResourcesMap() {
        Map<String, String> requestResources = new HashMap<>();
        if (this.getAccountId() != null) {
            requestResources.put(ResourceType.ACCOUNT, this.getAccountId().toString());
        }
        if (this.getCompetentAuthority() != null) {
            requestResources.put(ResourceType.CA, this.getCompetentAuthority().name());
        }
        if (this.getVerificationBodyId() != null) {
            requestResources.put(ResourceType.VERIFICATION_BODY, this.getVerificationBodyId().toString());
        }
        return requestResources;
    }
}
