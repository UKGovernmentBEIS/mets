package uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, AviationOutstandingRequestTasksRepository.class})
class AviationOutstandingRequestTasksRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationOutstandingRequestTasksRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findOutstandingRequestTaskByCaAndParams_without_user_ids() {
        UUID assigne1 = UUID.randomUUID();
        UUID assigne2 = UUID.randomUUID();
        UUID assigne3 = UUID.randomUUID();
        UUID assigne4 = UUID.randomUUID();
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        AviationAccount acc1 = createAccount(1L, "account1", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1EmpIssRequest = createRequest(acc1, "NEW1", RequestType.EMP_ISSUANCE_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc1EmpIssRequest, RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW, assigne1,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(5));

        AviationAccount acc2 = createAccount(2L, "account2", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2EmpVariationAppReview = createRequest(acc2, "NEW2", RequestType.EMP_VARIATION_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc2EmpVariationAppReview, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW, assigne2, null, null);

        AviationAccount acc3 = createAccount(3L, "account3", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc3EmpVariationAppReview = createRequest(acc3, "NEW3", RequestType.EMP_VARIATION_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc3EmpVariationAppReview, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW, assigne3, null, null);

        AviationAccount acc4 = createAccount(4L, "account4", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.SCOTLAND, legalEntity);
        Request acc4EmpVariationAppReview = createRequest(acc4, "NEW4", RequestType.EMP_VARIATION_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc4EmpVariationAppReview, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW, assigne4, null, null);

        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
                .requestTaskTypes(Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW.name(),
                        RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW.name()))
                .build();

        List<AviationOutstandingRequestTask> outstandingRequestTasks = repository.findOutstandingRequestTaskParams(entityManager, params);

        assertThat(outstandingRequestTasks).hasSize(4);

        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestId).containsOnly("NEW1", "NEW2", "NEW3", "NEW4");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getAccountId).containsOnly("EM00001", "EM00002", "EM00003", "EM00004");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getAccountName).containsOnly("account1", "account2", "account3", "account4");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getLegalEntityName).containsOnly("legalEntityName");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskType).containsOnly(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW.name(),
                RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW.name());
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskAssignee)
                .containsOnly(assigne1.toString(), assigne2.toString(), assigne3.toString(), assigne4.toString());
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskDueDate).containsOnly(LocalDate.now().plusDays(10), null, null, null);
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskRemainingDays).containsOnly(5L, null, null, null);
        final List<AviationOutstandingRequestTask> aviationOutstandingRequestTasks = outstandingRequestTasks.stream().map(AviationOutstandingRequestTask.class::cast).toList();
        assertThat(aviationOutstandingRequestTasks).extracting(AviationOutstandingRequestTask::getCrcoCode).containsOnly("1", "2", "3", "4");
    }

    @Test
    void findOutstandingRequestTaskByCaAndParams_with_provided_user_ids() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        UUID user3 = UUID.randomUUID();
        UUID user4 = UUID.randomUUID();
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        AviationAccount acc1 = createAccount(1L, "account1", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1EmpIssRequest = createRequest(acc1, "NEW1", RequestType.EMP_ISSUANCE_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc1EmpIssRequest, RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW, user1,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(5));

        AviationAccount acc2 = createAccount(2L, "account2", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2EmpVariationAppReview = createRequest(acc2, "NEW2", RequestType.EMP_VARIATION_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc2EmpVariationAppReview, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW, user2, null, null);

        AviationAccount acc3 = createAccount(3L, "account3", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc3EmpVariationAppReview = createRequest(acc3, "NEW3", RequestType.EMP_VARIATION_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc3EmpVariationAppReview, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW, user3, null, null);

        AviationAccount acc4 = createAccount(4L, "account4", AccountType.AVIATION, AviationAccountStatus.LIVE, CompetentAuthorityEnum.SCOTLAND, legalEntity);
        Request acc4EmpVariationAppReview = createRequest(acc4, "NEW4", RequestType.EMP_VARIATION_UKETS, RequestStatus.IN_PROGRESS);
        createRequestTask(acc4EmpVariationAppReview, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW, user4, null, null);

        OutstandingRegulatorRequestTasksMiReportParams params = OutstandingRegulatorRequestTasksMiReportParams.builder()
                .requestTaskTypes(Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW.name(),
                        RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW.name()))
                .userIds(Set.of(user1.toString(), user2.toString()))
                .build();

        List<AviationOutstandingRequestTask> outstandingRequestTasks = repository.findOutstandingRequestTaskParams(entityManager, params);

        assertThat(outstandingRequestTasks).hasSize(2);

        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestId).containsOnly("NEW1", "NEW2");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getAccountType).containsOnly(AccountType.AVIATION);
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getAccountName).containsOnly("account1", "account2");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getLegalEntityName).containsOnly("legalEntityName");
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskType).containsOnly(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW.name(),
                RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW.name());
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskAssignee)
                .containsOnly(user1.toString(), user2.toString());
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskDueDate).containsOnly(LocalDate.now().plusDays(10), null);
        assertThat(outstandingRequestTasks).extracting(AviationOutstandingRequestTask::getRequestTaskRemainingDays).containsOnly(5L, null);
        final List<AviationOutstandingRequestTask> aviationOutstandingRequestTasks = outstandingRequestTasks.stream().map(AviationOutstandingRequestTask.class::cast).toList();
        assertThat(aviationOutstandingRequestTasks).extracting(AviationOutstandingRequestTask::getCrcoCode).containsOnly("1", "2");

//        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
//        assertThat(outstandingRequestTasks.get(0).getAccountType()).isEqualTo(acc1.getAccountType());
//        assertThat(outstandingRequestTasks.get(0).getAccountName()).isEqualTo(acc1.getName());
//        assertThat(outstandingRequestTasks.get(0).getRequestId()).isEqualTo(acc1InstAccOpenRequest.getId());
//        assertThat(outstandingRequestTasks.get(0).getRequestTaskType()).isEqualTo(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
//        assertThat(outstandingRequestTasks.get(0).getRequestTaskAssignee()).isNotBlank();
//        assertThat(outstandingRequestTasks.get(0).getRequestTaskDueDate()).isEqualTo(LocalDate.now().plusDays(10));
//        assertThat(outstandingRequestTasks.get(0).getRequestTaskRemainingDays()).isEqualTo(5);
//
//        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
//        assertThat(outstandingRequestTasks.get(1).getAccountType()).isEqualTo(acc2.getAccountType());
//        assertThat(outstandingRequestTasks.get(1).getAccountName()).isEqualTo(acc2.getName());
//        assertThat(outstandingRequestTasks.get(1).getRequestId()).isEqualTo(acc2PermitVariationAppReview.getId());
//        assertThat(outstandingRequestTasks.get(1).getRequestTaskType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
//        assertThat(outstandingRequestTasks.get(1).getRequestTaskAssignee()).isNotBlank();
//        assertThat(outstandingRequestTasks.get(1).getRequestTaskDueDate()).isEqualTo(LocalDate.now().plusDays(10));
//        assertThat(outstandingRequestTasks.get(1).getRequestTaskRemainingDays()).isEqualTo(10);
    }

    private AviationAccount createAccount(Long id, String name, AccountType type, AviationAccountStatus status,
                                          CompetentAuthorityEnum competentAuthority, LegalEntity le) {

        AviationAccount account = AviationAccount.builder()
                .id(id)
                .name(name)
                .status(status)
                .accountType(type)
                .competentAuthority(competentAuthority)
                .legalEntity(le)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .commencementDate(LocalDate.of(2022, 1, 1))
                .emitterId("EM" + String.format("%05d", id))
                .location(getLocation())
                .crcoCode(String.valueOf(id))
                .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
                .build();

        entityManager.persist(account);
        return account;
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
