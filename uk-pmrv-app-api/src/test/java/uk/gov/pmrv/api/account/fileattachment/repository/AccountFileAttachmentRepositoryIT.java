package uk.gov.pmrv.api.account.fileattachment.repository;

import jakarta.persistence.EntityManager;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachment;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
class AccountFileAttachmentRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AccountFileAttachmentRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByWorkflowAndPeriodAndCompetentAuthority_returnsResults() {
        AccountFileAttachment file1 = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ1", AccountFileAttachmentStatus.IN_PROGRESS, 1L, "2024", "UUID1",
                CompetentAuthorityEnum.ENGLAND
        );

        AccountFileAttachment file2 = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ2", AccountFileAttachmentStatus.IN_PROGRESS, 2L, "2024", "UUID2",
                CompetentAuthorityEnum.ENGLAND
        );

        repo.save(file1);
        repo.save(file2);

        entityManager.flush();
        entityManager.clear();

        List<AccountFileAttachment> results =
                repo.findByWorkflowAndPeriodAndCompetentAuthority(
                        AccountFileAttachmentWorkflow.ALR,
                        "2024",
                        CompetentAuthorityEnum.ENGLAND
                );

        assertThat(results).hasSize(2);
        assertThat(results.stream().map(AccountFileAttachment::getFileUuid))
                .containsExactlyInAnyOrder("UUID1", "UUID2");
    }

    @Test
    void findByWorkflowAndPeriodAndCompetentAuthority_empty() {
        List<AccountFileAttachment> results =
                repo.findByWorkflowAndPeriodAndCompetentAuthority(
                        AccountFileAttachmentWorkflow.ALR,
                        "2050",
                        CompetentAuthorityEnum.ENGLAND
                );

        assertThat(results).isEmpty();
    }

    @Test
    void findByAccountIdAndWorkflowAndWorkflowSubtype_found() {
        String period = "2025";
        AccountFileAttachment entity = buildEntity(
                AccountFileAttachmentWorkflow.DOAL,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ77", AccountFileAttachmentStatus.IN_PROGRESS, 1L, period, "UUID77",
                CompetentAuthorityEnum.WALES
        );

        repo.save(entity);
        entityManager.flush();
        entityManager.clear();

        Optional<AccountFileAttachment> result =
                repo.findByAccountIdAndWorkflowAndWorkflowSubtypeAndPeriod(
                        1L, AccountFileAttachmentWorkflow.DOAL, AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT, period);

        assertThat(result).isPresent();
        assertThat(result.get().getFileUuid()).isEqualTo("UUID77");
    }

    @Test
    void findByAccountIdAndWorkflowAndWorkflowSubtype_empty() {
        Optional<AccountFileAttachment> result =
                repo.findByAccountIdAndWorkflowAndWorkflowSubtypeAndPeriod(
                        9999L, AccountFileAttachmentWorkflow.DOAL, AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT, "2050");

        assertThat(result).isEmpty();
    }

    @Test
    void findByFileUuid_found() {
        AccountFileAttachment entity = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ100", AccountFileAttachmentStatus.IN_PROGRESS, 100L, "2023", "UUID100",
                CompetentAuthorityEnum.SCOTLAND
        );

        repo.save(entity);
        entityManager.flush();
        entityManager.clear();

        Optional<AccountFileAttachment> result = repo.findByFileUuid("UUID100");

        assertThat(result).isPresent();
        assertThat(result.get().getOriginatedRequestId()).isEqualTo("REQ100");
    }

    @Test
    void findByFileUuid_empty() {
        Optional<AccountFileAttachment> result = repo.findByFileUuid("NOPE");
        assertThat(result).isEmpty();
    }

    private AccountFileAttachment buildEntity(
            AccountFileAttachmentWorkflow workflow,
            AccountFileAttachmentWorkflowSubType subtype,
            String requestId,
            AccountFileAttachmentStatus status,
            Long accountId,
            String period,
            String uuid,
            CompetentAuthorityEnum ca
    ) {
        AccountFileAttachment entity = new AccountFileAttachment();
        entity.setWorkflow(workflow);
        entity.setWorkflowSubtype(subtype);
        entity.setOriginatedRequestId(requestId);
        entity.setStatus(status);
        entity.setAccountId(accountId);
        entity.setPeriod(period);
        entity.setFileUuid(uuid);
        entity.setCompetentAuthority(ca);
        entity.setCreationDate(LocalDateTime.now());
        return entity;
    }

    @Test
    void findByWorkflowAndAccountId_returnsOnlyMatchingAccountAndWorkflow() {
        AccountFileAttachment acc1Alr = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ1", AccountFileAttachmentStatus.IN_PROGRESS, 1L, "2024", "UUID1",
                CompetentAuthorityEnum.ENGLAND
        );

        AccountFileAttachment acc1Doal = buildEntity(
                AccountFileAttachmentWorkflow.DOAL,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ2", AccountFileAttachmentStatus.IN_PROGRESS, 1L, "2024", "UUID2",
                CompetentAuthorityEnum.ENGLAND
        );

        AccountFileAttachment acc2Alr = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ3", AccountFileAttachmentStatus.IN_PROGRESS, 2L, "2024", "UUID3",
                CompetentAuthorityEnum.ENGLAND
        );

        repo.saveAll(List.of(acc1Alr, acc1Doal, acc2Alr));
        entityManager.flush();
        entityManager.clear();

        List<AccountFileAttachment> results =
                repo.findByWorkflowAndAccountId(AccountFileAttachmentWorkflow.ALR, 1L);

        assertThat(results).hasSize(1);
        assertThat(results.iterator().next().getFileUuid()).isEqualTo("UUID1");
    }

    @Test
    void findByAccountIdAndWorkflowInAndWorkflowSubtypeOrderByCreationDateDesc_returnsLatest() {
        AccountFileAttachment older = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ1", AccountFileAttachmentStatus.IN_PROGRESS, 1L, "2024", "UUID_OLD",
                CompetentAuthorityEnum.ENGLAND
        );
        older.setCreationDate(LocalDateTime.now().minusDays(2));

        AccountFileAttachment newer = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ2", AccountFileAttachmentStatus.IN_PROGRESS, 1L, "2024", "UUID_NEW",
                CompetentAuthorityEnum.ENGLAND
        );
        newer.setCreationDate(LocalDateTime.now());

        AccountFileAttachment differentWorkflow = buildEntity(
                AccountFileAttachmentWorkflow.DOAL,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ3", AccountFileAttachmentStatus.IN_PROGRESS, 1L, "2024", "UUID_OTHER",
                CompetentAuthorityEnum.ENGLAND
        );

        repo.saveAll(List.of(older, newer, differentWorkflow));
        entityManager.flush();
        entityManager.clear();

        List<AccountFileAttachment> results =
                repo.findByAccountIdAndWorkflowInAndWorkflowSubtypeAndStatusOrderByCreationDateDesc(
                        1L,
                        Set.of(AccountFileAttachmentWorkflow.ALR),
                        AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                        AccountFileAttachmentStatus.IN_PROGRESS
                );

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getFileUuid()).isEqualTo("UUID_NEW");
        assertThat(results.get(1).getFileUuid()).isEqualTo("UUID_OLD");
    }

    @Test
    void findDistinctPeriodsByWorkflow_returnsDistinctPeriodsOrderedDesc() {
        AccountFileAttachment a1 = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ1",
                AccountFileAttachmentStatus.IN_PROGRESS,
                1L,
                "2024",
                "UUID_1",
                CompetentAuthorityEnum.ENGLAND
        );

        AccountFileAttachment a2 = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ2",
                AccountFileAttachmentStatus.IN_PROGRESS,
                1L,
                "2023",
                "UUID_2",
                CompetentAuthorityEnum.ENGLAND
        );

        AccountFileAttachment a3 = buildEntity(
                AccountFileAttachmentWorkflow.ALR,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ3",
                AccountFileAttachmentStatus.IN_PROGRESS,
                1L,
                "2024", // duplicate period
                "UUID_3",
                CompetentAuthorityEnum.ENGLAND
        );

        AccountFileAttachment differentWorkflow = buildEntity(
                AccountFileAttachmentWorkflow.DOAL,
                AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                "REQ4",
                AccountFileAttachmentStatus.IN_PROGRESS,
                1L,
                "2022",
                "UUID_4",
                CompetentAuthorityEnum.ENGLAND
        );

        repo.saveAll(List.of(a1, a2, a3, differentWorkflow));
        entityManager.flush();
        entityManager.clear();

        List<String> results =
                repo.findDistinctPeriodsByWorkflowAndCA(AccountFileAttachmentWorkflow.ALR, CompetentAuthorityEnum.ENGLAND);

        assertThat(results).containsExactly("2024", "2023");
    }
}
