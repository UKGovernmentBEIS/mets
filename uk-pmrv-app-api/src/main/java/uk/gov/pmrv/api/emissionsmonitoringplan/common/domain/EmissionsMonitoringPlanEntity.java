package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Type;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "emp")
@NamedQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_QUERY_FIND_EMP_ACCOUNT_BY_ID,
    query = "select emp.accountId as accountId "
            + "from EmissionsMonitoringPlanEntity emp "
            + "where emp.id = :id")
@NamedQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_QUERY_FIND_EMP_ID_BY_ACCOUNT_ID,
    query = "select emp.id "
        + "from EmissionsMonitoringPlanEntity emp "
        + "where emp.accountId = :accountId"
)
@NamedNativeQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_NATIVE_QUERY_FIND_ALL_BY_ACCOUNT_IDS,
    query = "select emp.id as empId, emp.account_id as accountId "
        + "from emp emp "
        + "where account_id in (:accountIds)")
@NamedQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID_BY_ID,
    query = "update EmissionsMonitoringPlanEntity emp set emp.fileDocumentUuid = :fileDocumentUUid where emp.id = :empId"
)
@NamedQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID_BY_ACCOUNT_ID,
    query = "update EmissionsMonitoringPlanEntity emp set emp.fileDocumentUuid = :fileDocumentUUid where emp.accountId = :accountId"
)
@NamedQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_QUERY_UPDATE_CONSOLIDATION_NUMBER_BY_ACCOUNT_ID,
    query = "update EmissionsMonitoringPlanEntity emp set emp.consolidationNumber = :consolidationNumber where emp.accountId = :accountId"
)
@NamedNativeQuery(
    name = EmissionsMonitoringPlanEntity.NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID,
    query = "select emp.id as empId, emp.account_id as accountId "
        + "from emp emp "
        + "where emp.data->'empAttachments'->>:attachmentUuid is not null")
public class EmissionsMonitoringPlanEntity {


    public static final String NAMED_QUERY_FIND_EMP_ID_BY_ACCOUNT_ID = "EmissionsMonitoringPlanEntity.findEmpIdByAccountId";
    public static final String NAMED_NATIVE_QUERY_FIND_ALL_BY_ACCOUNT_IDS = "EmissionsMonitoringPlanEntity.findAllByAccountIdIn";

    public static final String NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID_BY_ID = "EmissionsMonitoringPlanEntity.updateFileDocumentUuidById";
    public static final String NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID_BY_ACCOUNT_ID = "EmissionsMonitoringPlanEntity.updateFileDocumentUuidByAccountId";

    public static final String NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID = "EmissionsMonitoringPlanEntity.findByAttachmentUuid";
    public static final String NAMED_QUERY_FIND_EMP_ACCOUNT_BY_ID = "EmissionsMonitoringPlanEntity.findEmpAccountById";

    public static final String NAMED_QUERY_UPDATE_CONSOLIDATION_NUMBER_BY_ACCOUNT_ID = "EmissionsMonitoringPlanEntity.updateConsolidationNumberByAccountId";

    public static final int CONSOLIDATION_NUMBER_DEFAULT_VALUE = 1;
    @Id
    private String id;

    @EqualsAndHashCode.Include
    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    @Valid
    private EmissionsMonitoringPlanContainer empContainer;

    @Column(name = "consolidation_number")
    @NotNull
    private int consolidationNumber;

    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    public EmissionsMonitoringPlanEntity(String id, Long accountId, EmissionsMonitoringPlanContainer empContainer, String fileDocumentUuid) {
        this.id = id;
        this.accountId = accountId;
        this.empContainer = empContainer;
        this.fileDocumentUuid = fileDocumentUuid;
        this.consolidationNumber = CONSOLIDATION_NUMBER_DEFAULT_VALUE;
    }

    @Builder
    public static EmissionsMonitoringPlanEntity createEmissionsMonitoringPlanEntity(String id, Long accountId,
                                                                                    EmissionsMonitoringPlanContainer empContainer,
                                                                                    String fileDocumentUuid) {
        return new EmissionsMonitoringPlanEntity(id, accountId, empContainer, fileDocumentUuid);
    }
}
