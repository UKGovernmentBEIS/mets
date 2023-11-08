package uk.gov.pmrv.api.permit.domain;

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
@Table(name = "permit")
@NamedQuery(
    name = PermitEntity.NAMED_QUERY_FIND_PERMIT_ACCOUNT_BY_ID,
    query = "select p.accountId as accountId "
        + "from PermitEntity p "
        + "where p.id = :id")
@NamedNativeQuery(
    name = PermitEntity.NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID,
    query = "select p.id as permitEntityId, p.account_id as accountId "
        + "from permit p "
        + "where p.data->'permitAttachments'->>:attachmentUuid is not null")
@NamedNativeQuery(
    name = PermitEntity.NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_IDS,
    query = "select p.id as permitEntityId, p.account_id as accountId, CAST(p.data->>'permitType' as varchar) as permitType "
        + "from permit p "
        + "where account_id in(:accountIds)")
@NamedQuery(
    name = PermitEntity.NAMED_QUERY_FIND_PERMIT_ID_BY_ACCOUNT_ID,
    query = "select p.id "
        + "from PermitEntity p "
        + "where p.accountId = :accountId")
@NamedQuery(
    name = PermitEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID,
    query = "update PermitEntity p set p.fileDocumentUuid = :fileDocumentUUid where p.id = :permitId"
)
public class PermitEntity {

    public static final String NAMED_QUERY_FIND_PERMIT_ACCOUNT_BY_ID = "PermitEntity.findPermitAccountById";
    public static final String NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID = "PermitEntity.findByAttachmentUuid";
    public static final String NAMED_QUERY_FIND_PERMIT_ID_BY_ACCOUNT_ID = "PermitEntity.findPermitIdByAccountId";
    public static final String NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_IDS = "PermitEntity.findPermitsByAccountIds";
    public static final String NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID = "PermitEntity.updateFileDocumentUuid";
    public static final int CONSOLIDATION_NUMBER_DEFAULT_VALUE = 1;

    @Id
    private String id;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    @Valid
    private PermitContainer permitContainer;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @Column(name = "consolidation_number")
    private int consolidationNumber;

    @Column(name = "file_document_uuid")
    private String fileDocumentUuid;

    private PermitEntity(String id, PermitContainer permitContainer, Long accountId, String fileDocumentUuid) {
        this.id = id;
        this.permitContainer = permitContainer;
        this.accountId = accountId;
        this.consolidationNumber = CONSOLIDATION_NUMBER_DEFAULT_VALUE;
        this.fileDocumentUuid = fileDocumentUuid;
    }

    @Builder
    public static PermitEntity createPermit(String id, PermitContainer permitContainer, Long accountId, String fileDocumentUuid) {
        return new PermitEntity(id, permitContainer, accountId, fileDocumentUuid);
    }
}
