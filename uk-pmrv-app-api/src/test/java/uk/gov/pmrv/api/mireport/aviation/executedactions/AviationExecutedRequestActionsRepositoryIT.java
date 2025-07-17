package uk.gov.pmrv.api.mireport.aviation.executedactions;

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
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestAction;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsMiReportParams;
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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, AviationExecutedRequestActionsRepository.class})
class AviationExecutedRequestActionsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationExecutedRequestActionsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findExecutedRequestActions_results_when_only_mandatory_parameters_applied(){
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        AviationAccount acc1 = createAccount(1L,"account", AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1Request = createRequest(acc1, "NEW1", RequestType.EMP_ISSUANCE_UKETS, RequestStatus.COMPLETED);
        createRequestAction(acc1Request,
            RequestActionType.PAYMENT_MARKED_AS_PAID,
            LocalDateTime.of(2022, 1, 5, 12, 30 ),
            "operator");
        RequestAction acc1RequestAction = createRequestAction(acc1Request,
            RequestActionType.RDE_SUBMITTED,
            LocalDateTime.of(2022, 1, 6, 15, 45 ),
            "regulator");
        EmissionsMonitoringPlanEntity acc1Emp = createEmp(1L, "UK-W-AV-000001");

        AviationAccount acc2 = createAccount(2L,"account2", AviationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc2Request = createRequest(acc2, "NEW2", RequestType.EMP_ISSUANCE_UKETS, RequestStatus.IN_PROGRESS);
        createRequestAction(acc2Request,
            RequestActionType.PAYMENT_MARKED_AS_PAID,
            LocalDateTime.of(2022, 1, 5, 22, 30 ),
            "operator");
        RequestAction acc2RequestAction = createRequestAction(acc2Request,
            RequestActionType.RDE_SUBMITTED,
            LocalDateTime.of(2022, 1, 10, 15, 45 ),
            "regulator");

        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.of(2022,1,6))
            .build();

        List<AviationExecutedRequestAction> actions = repository.findExecutedRequestActions(entityManager, reportParams);

        assertThat(actions).isNotEmpty();
        assertThat(actions).hasSize(2);

        AviationExecutedRequestAction executedRequestAction = actions.get(0);
        assertEquals(acc1.getEmitterId(), executedRequestAction.getAccountId());
        assertEquals(acc1.getAccountType(), executedRequestAction.getAccountType());
        assertEquals(acc1.getStatus().getName(), executedRequestAction.getAccountStatus());
        assertEquals(acc1.getName(), executedRequestAction.getAccountName());
        assertEquals(acc1.getName(), executedRequestAction.getAccountName());
        assertEquals(legalEntity.getName(), executedRequestAction.getLegalEntityName());
        assertEquals(acc1Request.getId(), executedRequestAction.getRequestId());
        assertEquals(acc1Request.getType().name(), executedRequestAction.getRequestType());
        assertEquals(acc1Request.getStatus().name(), executedRequestAction.getRequestStatus());
        assertEquals(acc1RequestAction.getType().name(), executedRequestAction.getRequestActionType());
        assertEquals(acc1RequestAction.getSubmitter(), executedRequestAction.getRequestActionSubmitter());
        assertEquals(acc1RequestAction.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
            executedRequestAction.getRequestActionCompletionDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(acc1Emp.getId(), executedRequestAction.getPermitId());
        assertEquals(acc1.getCrcoCode(), executedRequestAction.getCrcoCode());

        executedRequestAction = actions.get(1);
        assertEquals(acc2.getEmitterId(), executedRequestAction.getAccountId());
        assertEquals(acc2.getAccountType(), executedRequestAction.getAccountType());
        assertEquals(acc2.getStatus().getName(), executedRequestAction.getAccountStatus());
        assertEquals(acc2.getName(), executedRequestAction.getAccountName());
        assertEquals(acc2.getName(), executedRequestAction.getAccountName());
        assertEquals(legalEntity.getName(), executedRequestAction.getLegalEntityName());
        assertEquals(acc2Request.getId(), executedRequestAction.getRequestId());
        assertEquals(acc2Request.getType().name(), executedRequestAction.getRequestType());
        assertEquals(acc2Request.getStatus().name(), executedRequestAction.getRequestStatus());
        assertEquals(acc2RequestAction.getType().name(), executedRequestAction.getRequestActionType());
        assertEquals(acc2RequestAction.getSubmitter(), executedRequestAction.getRequestActionSubmitter());
        assertEquals(acc2RequestAction.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
            executedRequestAction.getRequestActionCompletionDate().truncatedTo(ChronoUnit.MILLIS));
        assertNull(executedRequestAction.getPermitId());
        assertEquals(acc2.getCrcoCode(), executedRequestAction.getCrcoCode());
    }

    @Test
    void findExecutedRequestActions_results_when_all_parameters_applied(){
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        AviationAccount acc1 = createAccount(1L,"account", AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1Request = createRequest(acc1, "NEW1", RequestType.EMP_ISSUANCE_UKETS, RequestStatus.COMPLETED);
        createRequestAction(acc1Request,
            RequestActionType.PAYMENT_MARKED_AS_RECEIVED,
            LocalDateTime.of(2022, 1, 5, 0, 0 ),
            "operator");
        createRequestAction(acc1Request,
            RequestActionType.RDE_ACCEPTED,
            LocalDateTime.of(2022, 1, 6, 15, 45 ),
            "regulator");

        AviationAccount acc2 = createAccount(2L,"account2", AviationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2Request = createRequest(acc2, "NEW2", RequestType.EMP_ISSUANCE_UKETS, RequestStatus.IN_PROGRESS);
        createRequestAction(acc2Request,
            RequestActionType.PAYMENT_MARKED_AS_RECEIVED,
            LocalDateTime.of(2022, 1, 10, 0, 30 ),
            "operator");

        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.of(2022,1,5))
            .toDate(LocalDate.of(2022,1,10))
            .build();

        List<AviationExecutedRequestAction> actions = repository.findExecutedRequestActions(entityManager, reportParams);
        final List<AviationExecutedRequestAction> aviationExecutedRequestActions = actions.stream()
                .map(AviationExecutedRequestAction.class::cast)
                .toList();

        assertThat(actions).isNotEmpty();
        assertThat(actions).hasSize(2);
        assertThat(actions).extracting(ExecutedRequestAction::getAccountId).containsOnly(acc1.getEmitterId());
        assertThat(aviationExecutedRequestActions).extracting(AviationExecutedRequestAction::getCrcoCode).containsOnly(acc1.getCrcoCode());
    }

    private LegalEntity createLegalEntity(String name) {
        LegalEntity le = LegalEntity.builder()
            .location(LocationOnShore.builder()
                .address(Address.builder()
                    .city("city")
                    .country("GR")
                    .line1("line")
                    .postcode("postcode")
                    .build())
                .build())
            .name(name)
            .status(LegalEntityStatus.ACTIVE)
            .type(LegalEntityType.LIMITED_COMPANY)
            .build();
        entityManager.persist(le);
        return le;
    }

    private AviationAccount createAccount(Long id, String name, AviationAccountStatus status,
                                          CompetentAuthorityEnum competentAuthority, LegalEntity le) {

        AviationAccount account = AviationAccount.builder()
            .id(id)
            .name(name)
            .status(status)
            .reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
            .accountType(AccountType.AVIATION)
            .competentAuthority(competentAuthority)
            .legalEntity(le)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .commencementDate(LocalDate.of(2022, 1, 1))
            .emitterId("EM" + String.format("%05d", id))
            .crcoCode(String.valueOf(id))
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

    private RequestAction createRequestAction(Request request, RequestActionType type, LocalDateTime creationDate, String submitter) {
        RequestAction requestAction = RequestAction.builder()
            .request(request)
            .type(type)
            .submitter(submitter)
            .build();

        entityManager.persist(requestAction);

        requestAction.setCreationDate(creationDate);
        entityManager.merge(requestAction);

        return requestAction;
    }

    private EmissionsMonitoringPlanEntity createEmp(Long accountId, String empId) {
        EmissionsMonitoringPlanEntity emp = EmissionsMonitoringPlanEntity.builder().id(empId).accountId(accountId).build();
        entityManager.persist(emp);
        return emp;
    }
}