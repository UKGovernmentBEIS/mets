package uk.gov.pmrv.api.mireport.installation.executedactions;

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
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
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

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, InstallationExecutedRequestActionsRepository.class})
class InstallationExecutedRequestActionsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private InstallationExecutedRequestActionsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findRequestActionsByCaAndParams_resultes_when_only_mandatory_parameters_applied(){
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account acc1 = createAccount(1L,"account", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1InstAccOpenRequest = createRequest(acc1, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED);
        createRequestAction(acc1InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 5, 12, 30 ),
            "operator");
        RequestAction acc1InstAccOpenRequestApproved = createRequestAction(acc1InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
            LocalDateTime.of(2022, 1, 6, 15, 45 ),
            "regulator");

        Account acc2 = createAccount(2L,"account2", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);
        Request acc2InstAccOpenRequest = createRequest(acc2, "NEW2", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS);
        createRequestAction(acc2InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 5, 22, 30 ),
            "operator");
        RequestAction acc2InstAccOpenRequestApproved = createRequestAction(acc2InstAccOpenRequest,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
                LocalDateTime.of(2022, 1, 10, 15, 45 ),
                "regulator");

        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.of(2022,1,6))
            .build();

        List<InstallationExecutedRequestAction> actions = repository.findExecutedRequestActions(entityManager, reportParams);

        assertThat(actions).isNotEmpty();
        assertThat(actions).hasSize(2);

        InstallationExecutedRequestAction executedRequestAction = actions.get(0);
        assertEquals(acc1.getEmitterId(), executedRequestAction.getAccountId());
        assertEquals(acc1.getAccountType(), executedRequestAction.getAccountType());
        assertEquals(acc1.getStatus().getName(), executedRequestAction.getAccountStatus());
        assertEquals(acc1.getName(), executedRequestAction.getAccountName());
        assertEquals(acc1.getName(), executedRequestAction.getAccountName());
        assertEquals(legalEntity.getName(), executedRequestAction.getLegalEntityName());
        assertEquals(acc1InstAccOpenRequest.getId(), executedRequestAction.getRequestId());
        assertEquals(acc1InstAccOpenRequest.getType().name(), executedRequestAction.getRequestType());
        assertEquals(acc1InstAccOpenRequest.getStatus().name(), executedRequestAction.getRequestStatus());
        assertEquals(acc1InstAccOpenRequestApproved.getType().name(), executedRequestAction.getRequestActionType());
        assertEquals(acc1InstAccOpenRequestApproved.getSubmitter(), executedRequestAction.getRequestActionSubmitter());
        assertEquals(acc1InstAccOpenRequestApproved.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
            executedRequestAction.getRequestActionCompletionDate().truncatedTo(ChronoUnit.MILLIS));

        executedRequestAction = actions.get(1);
        assertEquals(acc2.getEmitterId(), executedRequestAction.getAccountId());
        assertEquals(acc2.getAccountType(), executedRequestAction.getAccountType());
        assertEquals(acc2.getStatus().getName(), executedRequestAction.getAccountStatus());
        assertEquals(acc2.getName(), executedRequestAction.getAccountName());
        assertEquals(acc2.getName(), executedRequestAction.getAccountName());
        assertEquals(legalEntity.getName(), executedRequestAction.getLegalEntityName());
        assertEquals(acc2InstAccOpenRequest.getId(), executedRequestAction.getRequestId());
        assertEquals(acc2InstAccOpenRequest.getType().name(), executedRequestAction.getRequestType());
        assertEquals(acc2InstAccOpenRequest.getStatus().name(), executedRequestAction.getRequestStatus());
        assertEquals(acc2InstAccOpenRequestApproved.getType().name(), executedRequestAction.getRequestActionType());
        assertEquals(acc2InstAccOpenRequestApproved.getSubmitter(), executedRequestAction.getRequestActionSubmitter());
        assertEquals(acc2InstAccOpenRequestApproved.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                executedRequestAction.getRequestActionCompletionDate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void findRequestActionsByCaAndParams_results_when_all_parameters_applied(){
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account acc1 = createAccount(1L,"account", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1InstAccOpenRequest = createRequest(acc1, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED);
        createRequestAction(acc1InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 5, 0, 0 ),
            "operator");
        createRequestAction(acc1InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
            LocalDateTime.of(2022, 1, 6, 15, 45 ),
            "regulator");

        Account acc2 = createAccount(2L,"account2", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2InstAccOpenRequest = createRequest(acc2, "NEW2", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS);
        createRequestAction(acc2InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 10, 0, 30 ),
            "operator");

        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.of(2022,1,5))
            .toDate(LocalDate.of(2022,1,10))
            .build();

        List<InstallationExecutedRequestAction> actions = repository.findExecutedRequestActions(entityManager, reportParams);

        assertThat(actions).isNotEmpty();
        assertThat(actions).hasSize(2);
        assertThat(actions).extracting(ExecutedRequestAction::getAccountId).containsOnly(acc1.getEmitterId());
    }

    @Test
    void findRequestActionsByCaAndParams_results_fetched_on_requested_order() {
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account acc1 = createAccount(1L,"account1", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc1InstAccOpenRequest1 = createRequest(acc1, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED);
        RequestAction acc1InstAccOpenRequest1Submitted = createRequestAction(acc1InstAccOpenRequest1,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 1, 12, 30 ),
            "operator");
        RequestAction acc1InstAccOpenRequest1Approved = createRequestAction(acc1InstAccOpenRequest1,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
            LocalDateTime.of(2022, 1, 3, 15, 45 ),
            "regulator");
        Request acc1InstAccOpenRequest2 = createRequest(acc1, "NEW2", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS);
        RequestAction acc1InstAccOpenRequest2Submitted = createRequestAction(acc1InstAccOpenRequest2,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 2, 10, 0),
            "operator");
        Request acc1PermitIssuanceRequest = createRequest(acc1, "PERM1", RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS);
        RequestAction acc1PermitIssuanceRequestGranted = createRequestAction(acc1PermitIssuanceRequest,
            RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 15, 10, 30),
            "operator");

        Account acc2 = createAccount(2L,"account2", InstallationAccountStatus.NEW, CompetentAuthorityEnum.WALES, legalEntity);
        Request acc2InstAccOpenRequest = createRequest(acc2, "NEW3", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED);
        createRequestAction(acc2InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 2, 0, 0 ),
            "operator");
        createRequestAction(acc2InstAccOpenRequest,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
            LocalDateTime.of(2022, 1, 6, 11, 0 ),
            "regulator");

        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.of(2022,1,1))
            .build();

        List<InstallationExecutedRequestAction> actions = repository.findExecutedRequestActions(entityManager, reportParams);

        assertThat(actions).isNotEmpty();
        assertThat(actions).hasSize(6);
        assertThat(actions).extracting(ExecutedRequestAction::getAccountId)
            .containsExactly(acc1.getEmitterId(), acc1.getEmitterId(), acc1.getEmitterId(), acc1.getEmitterId(), acc2.getEmitterId(), acc2.getEmitterId());
        assertThat(actions)
            .filteredOn(action -> action.getAccountId().equals(acc1.getEmitterId()))
            .extracting(InstallationExecutedRequestAction::getRequestType)
            .containsExactly(
                RequestType.INSTALLATION_ACCOUNT_OPENING.name(),
                RequestType.INSTALLATION_ACCOUNT_OPENING.name(),
                RequestType.INSTALLATION_ACCOUNT_OPENING.name(),
                RequestType.PERMIT_ISSUANCE.name()
            );
        assertThat(actions)
            .filteredOn(action -> action.getAccountId().equals(acc1.getEmitterId())
                && action.getRequestType().equals(RequestType.INSTALLATION_ACCOUNT_OPENING.name())
            )
            .extracting(ExecutedRequestAction::getRequestId)
            .containsExactly(
                acc1InstAccOpenRequest1.getId(),
                acc1InstAccOpenRequest1.getId(),
                acc1InstAccOpenRequest2.getId()
            );
        assertThat(actions)
            .filteredOn(action -> action.getAccountId().equals(acc1.getEmitterId())
                && action.getRequestType().equals(RequestType.INSTALLATION_ACCOUNT_OPENING.name())
                && action.getRequestId().equals(acc1InstAccOpenRequest1.getId())
            )
            .extracting(ExecutedRequestAction::getRequestActionCompletionDate)
            .containsExactly(
                acc1InstAccOpenRequest1Submitted.getCreationDate(),
                acc1InstAccOpenRequest1Approved.getCreationDate()
            );
    }

    @Test
    void findRequestActionsByCaAndParams_no_results(){
        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account account = createAccount(1L,"account", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.WALES, legalEntity);
        Request request = createRequest(account, "NEW1", RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED);
        createRequestAction(request,
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            LocalDateTime.of(2022, 1, 15, 12, 30 ),
            "operator");

        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder()
            .reportType(MiReportType.COMPLETED_WORK)
            .fromDate(LocalDate.of(2022,1,16))
            .build();

        List<InstallationExecutedRequestAction> executedRequestActions =
            repository.findExecutedRequestActions(entityManager, reportParams);

        assertThat(executedRequestActions).isEmpty();
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

    private InstallationAccount createAccount(Long id, String name, InstallationAccountStatus status,
                                              CompetentAuthorityEnum competentAuthority, LegalEntity le) {

        InstallationAccount account = InstallationAccount.builder()
            .id(id)
            .name(name)
            .status(status)
            .accountType(AccountType.INSTALLATION)
            .applicationType(ApplicationType.NEW_PERMIT)
            .siteName(name)
            .competentAuthority(competentAuthority)
            .legalEntity(le)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
            .commencementDate(LocalDate.of(2022, 1, 1))
            .emitterId("EM" + String.format("%05d", id))
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
}