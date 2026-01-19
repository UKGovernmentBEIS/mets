package uk.gov.pmrv.api.account.fileattachment.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachment;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountFileAttachmentRepository extends JpaRepository<AccountFileAttachment, Long> {

    @Transactional(readOnly = true)
    List<AccountFileAttachment> findByWorkflowAndPeriodAndCompetentAuthority(
            AccountFileAttachmentWorkflow workflow, String period, CompetentAuthorityEnum competentAuthority);

    @Transactional(readOnly = true)
    Optional<AccountFileAttachment> findByAccountIdAndWorkflowAndWorkflowSubtypeAndPeriod(Long accountId, AccountFileAttachmentWorkflow workflow, AccountFileAttachmentWorkflowSubType workflowSubtype, String period);

    @Transactional(readOnly = true)
    Optional<AccountFileAttachment> findByFileUuid(String fileUuid);

    @Transactional
    List<AccountFileAttachment> findByWorkflowAndAccountId(
            AccountFileAttachmentWorkflow workflow, Long accountId
    );

    @Transactional(readOnly = true)
    List<AccountFileAttachment> findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(
            Long accountId, Set<AccountFileAttachmentWorkflow> workflows, AccountFileAttachmentWorkflowSubType workflowSubtype, AccountFileAttachmentStatus status
    );

    @Transactional(readOnly = true)
    @Query("""
    select distinct a.period
    from AccountFileAttachment a
    where a.workflow = :workflow
    and a.competentAuthority = :competentAuthority
    order by a.period desc
    """)
    List<String> findDistinctPeriodsByWorkflowAndCA(
            @Param("workflow") AccountFileAttachmentWorkflow workflow,
            @Param("competentAuthority") CompetentAuthorityEnum competentAuthority
    );

    @Transactional(readOnly = true)
    boolean existsByWorkflowAndCompetentAuthority(AccountFileAttachmentWorkflow workflow, CompetentAuthorityEnum competentAuthority);
}
