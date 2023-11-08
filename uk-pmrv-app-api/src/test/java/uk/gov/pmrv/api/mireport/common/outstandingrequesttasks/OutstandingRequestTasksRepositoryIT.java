package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, OutstandingRequestTasksRepository.class})
class OutstandingRequestTasksRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private OutstandingRequestTasksRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findOutstandingRequestTaskByCaAndParams_without_user_ids() {
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account acc1 = createAccount(1L, "account", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1InstAccOpenRequest = createRequest(acc1, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS);
        createRequestTask(acc1InstAccOpenRequest, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, UUID.randomUUID(),
            LocalDate.now().plusDays(10), LocalDate.now().plusDays(5));

        Account acc2 = createAccount(2L, "account2", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2PermitVariationAppReview = createRequest(acc2, "NEW2", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        createRequestTask(acc2PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, UUID.randomUUID(), null, null);

        Account acc3 = createAccount(3L, "account3", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc3PermitVariationAppReview = createRequest(acc3, "NEW3", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        createRequestTask(acc3PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, UUID.randomUUID(), null, null);

        Account acc4 = createAccount(4L, "account4", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.SCOTLAND, legalEntity);
        Request acc4PermitVariationAppReview = createRequest(acc4, "NEW4", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        createRequestTask(acc4PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, UUID.randomUUID(), null, null);

        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
            .requestTaskTypes(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW))
            .build();

        List<OutstandingRequestTask> outstandingRequestTasks = repository.findOutstandingRequestTaskParams(entityManager, params);

        assertThat(outstandingRequestTasks).hasSize(4);

        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
        assertThat(outstandingRequestTasks.get(0).getAccountType()).isEqualTo(acc1.getAccountType());
        assertThat(outstandingRequestTasks.get(0).getAccountName()).isEqualTo(acc1.getName());
        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
        assertThat(outstandingRequestTasks.get(0).getLegalEntityName()).isEqualTo(acc1.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(0).getRequestTaskType()).isEqualTo(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(0).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(0).getRequestTaskDueDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(outstandingRequestTasks.get(0).getRequestTaskRemainingDays()).isEqualTo(5);

        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(1).getAccountType()).isEqualTo(acc2.getAccountType());
        assertThat(outstandingRequestTasks.get(1).getAccountName()).isEqualTo(acc2.getName());
        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(1).getLegalEntityName()).isEqualTo(acc2.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(1).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(1).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(1).getRequestTaskDueDate()).isNull();
        assertThat(outstandingRequestTasks.get(1).getRequestTaskRemainingDays()).isNull();

        assertThat(outstandingRequestTasks.get(2).getRequestId()).isEqualTo(acc3PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(2).getAccountType()).isEqualTo(acc3.getAccountType());
        assertThat(outstandingRequestTasks.get(2).getAccountName()).isEqualTo(acc3.getName());
        assertThat(outstandingRequestTasks.get(2).getRequestId()).isEqualTo(acc3PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(2).getLegalEntityName()).isEqualTo(acc3.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(2).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(2).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(2).getRequestTaskDueDate()).isNull();
        assertThat(outstandingRequestTasks.get(2).getRequestTaskRemainingDays()).isNull();

        assertThat(outstandingRequestTasks.get(3).getRequestId()).isEqualTo(acc4PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(3).getAccountType()).isEqualTo(acc4.getAccountType());
        assertThat(outstandingRequestTasks.get(3).getAccountName()).isEqualTo(acc4.getName());
        assertThat(outstandingRequestTasks.get(3).getRequestId()).isEqualTo(acc4PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(3).getLegalEntityName()).isEqualTo(acc4.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(3).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(3).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(3).getRequestTaskDueDate()).isNull();
        assertThat(outstandingRequestTasks.get(3).getRequestTaskRemainingDays()).isNull();
    }

    @Test
    public void findOutstandingRequestTaskByCaAndParams_without_user_ids_and_without_account_type() {
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account acc1 = createAccount(1L, "account", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc1InstAccOpenRequest = createRequest(acc1, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS);
        createRequestTask(acc1InstAccOpenRequest, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, UUID.randomUUID(),
            LocalDate.now().plusDays(10), LocalDate.now().plusDays(5));

        Account acc2 = createAccount(2L, "account2", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc2PermitVariationAppReview = createRequest(acc2, "NEW2", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        createRequestTask(acc2PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, UUID.randomUUID(), null, null);

        Account acc3 = createAccount(3L, "account3", AccountType.AVIATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc3PermitVariationAppReview = createRequest(acc3, "NEW3", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        createRequestTask(acc3PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, UUID.randomUUID(), null, null);

        Account acc4 = createAccount(4L, "account4", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.SCOTLAND, legalEntity);
        Request acc4PermitVariationAppReview = createRequest(acc4, "NEW4", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        createRequestTask(acc4PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, UUID.randomUUID(), null, null);

        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
            .requestTaskTypes(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW))
            .build();

        List<OutstandingRequestTask> outstandingRequestTasks = repository.findOutstandingRequestTaskParams(entityManager, params);

        assertThat(outstandingRequestTasks).hasSize(4);

        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
        assertThat(outstandingRequestTasks.get(0).getAccountType()).isEqualTo(acc1.getAccountType());
        assertThat(outstandingRequestTasks.get(0).getAccountName()).isEqualTo(acc1.getName());
        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
        assertThat(outstandingRequestTasks.get(0).getLegalEntityName()).isEqualTo(acc1.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(0).getRequestTaskType()).isEqualTo(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(0).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(0).getRequestTaskDueDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(outstandingRequestTasks.get(0).getRequestTaskRemainingDays()).isEqualTo(5);

        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(1).getAccountType()).isEqualTo(acc2.getAccountType());
        assertThat(outstandingRequestTasks.get(1).getAccountName()).isEqualTo(acc2.getName());
        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(1).getLegalEntityName()).isEqualTo(acc2.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(1).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(1).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(1).getRequestTaskDueDate()).isNull();
        assertThat(outstandingRequestTasks.get(1).getRequestTaskRemainingDays()).isNull();

        assertThat(outstandingRequestTasks.get(2).getRequestId()).isEqualTo(acc3PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(2).getAccountType()).isEqualTo(acc3.getAccountType());
        assertThat(outstandingRequestTasks.get(2).getAccountName()).isEqualTo(acc3.getName());
        assertThat(outstandingRequestTasks.get(2).getRequestId()).isEqualTo(acc3PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(2).getLegalEntityName()).isEqualTo(acc3.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(2).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(2).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(2).getRequestTaskDueDate()).isNull();
        assertThat(outstandingRequestTasks.get(2).getRequestTaskRemainingDays()).isNull();

        assertThat(outstandingRequestTasks.get(3).getRequestId()).isEqualTo(acc4PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(3).getAccountType()).isEqualTo(acc4.getAccountType());
        assertThat(outstandingRequestTasks.get(3).getEmitterId()).isEqualTo(acc4.getEmitterId());
        assertThat(outstandingRequestTasks.get(3).getRequestId()).isEqualTo(acc4PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(3).getLegalEntityName()).isEqualTo(acc4.getLegalEntity().getName());
        assertThat(outstandingRequestTasks.get(3).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(3).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(3).getRequestTaskDueDate()).isNull();
        assertThat(outstandingRequestTasks.get(3).getRequestTaskRemainingDays()).isNull();
    }

    @Test
    public void findOutstandingRequestTaskByCaAndParams_with_provided_user_ids() {
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account acc1 = createAccount(1L, "account", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1InstAccOpenRequest = createRequest(acc1, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS);
        UUID user1 = UUID.randomUUID();
        createRequestTask(acc1InstAccOpenRequest, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, user1,
            LocalDate.now().plusDays(10), LocalDate.now().plusDays(5));

        Account acc2 = createAccount(2L, "account2", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2PermitVariationAppReview = createRequest(acc2, "NEW2", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        UUID user2 = UUID.randomUUID();
        createRequestTask(acc2PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, user2,
            LocalDate.now().plusDays(10), null);

        Account acc3 = createAccount(3L, "account3", AccountType.INSTALLATION, InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc3PermitVariationAppReview = createRequest(acc3, "NEW3", RequestType.PERMIT_VARIATION, RequestStatus.IN_PROGRESS);
        UUID user3 = UUID.randomUUID();
        createRequestTask(acc3PermitVariationAppReview, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW, user3,
            LocalDate.now().plusDays(10), null);

        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
            .requestTaskTypes(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW))
            .userIds(Set.of(user1.toString(), user2.toString()))
            .build();

        List<OutstandingRequestTask> outstandingRequestTasks = repository.findOutstandingRequestTaskParams(entityManager, params);

        assertThat(outstandingRequestTasks.size()).isEqualTo(2);

        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
        assertThat(outstandingRequestTasks.get(0).getAccountType()).isEqualTo(acc1.getAccountType());
        assertThat(outstandingRequestTasks.get(0).getAccountName()).isEqualTo(acc1.getName());
        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
        assertThat(outstandingRequestTasks.get(0).getRequestTaskType()).isEqualTo(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(0).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(0).getRequestTaskDueDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(outstandingRequestTasks.get(0).getRequestTaskRemainingDays()).isEqualTo(5);

        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(1).getAccountType()).isEqualTo(acc2.getAccountType());
        assertThat(outstandingRequestTasks.get(1).getAccountName()).isEqualTo(acc2.getName());
        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
        assertThat(outstandingRequestTasks.get(1).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
        assertThat(outstandingRequestTasks.get(1).getRequestTaskAssignee()).isNotBlank();
        assertThat(outstandingRequestTasks.get(1).getRequestTaskDueDate()).isEqualTo(LocalDate.now().plusDays(10));
        assertThat(outstandingRequestTasks.get(1).getRequestTaskRemainingDays()).isEqualTo(10);
    }

    private LegalEntity createLegalEntity(String name) {
        LegalEntity le = LegalEntity.builder()
            .location(getLocation())
            .name(name)
            .status(LegalEntityStatus.ACTIVE)
            .type(LegalEntityType.LIMITED_COMPANY)
            .build();
        entityManager.persist(le);
        return le;
    }

    private InstallationAccount createAccount(Long id, String name, AccountType type, InstallationAccountStatus status,
                                              CompetentAuthorityEnum competentAuthority, LegalEntity le) {

        InstallationAccount account = InstallationAccount.builder()
            .id(id)
            .name(name)
            .status(status)
            .accountType(type)
            .applicationType(ApplicationType.NEW_PERMIT)
            .siteName(name)
            .competentAuthority(competentAuthority)
            .legalEntity(le)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
            .commencementDate(LocalDate.of(2022, 1, 1))
            .emitterId("EM" + String.format("%05d", id))
            .location(getLocation())
            .build();

        entityManager.persist(account);
        return account;
    }

    private Request createRequest(Account account, String requestId, RequestType type, RequestStatus status) {
        Request request = Request.builder()
            .id(requestId)
            .type(type)
            .status(status)
            .accountId(account.getId())
            .competentAuthority(account.getCompetentAuthority())
            .build();

        entityManager.persist(request);
        return request;
    }

    private RequestTask createRequestTask(Request request, RequestTaskType type, UUID assignee, LocalDate dueDate, LocalDate pauseDate) {
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(type)
            .assignee(assignee.toString())
            .startDate(LocalDateTime.now())
            .pauseDate(pauseDate)
            .processTaskId(UUID.randomUUID().toString())
            .dueDate(dueDate)
            .build();

        entityManager.persist(requestTask);

        return requestTask;
    }

    private LocationOnShore getLocation() {
        return LocationOnShore.builder()
            .address(Address.builder()
                .city("city")
                .country("GR")
                .line1("line")
                .postcode("postcode")
                .build())
            .build();
    }
}
