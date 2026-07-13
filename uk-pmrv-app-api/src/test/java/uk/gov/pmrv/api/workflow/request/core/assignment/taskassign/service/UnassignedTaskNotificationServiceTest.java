package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnassignedTaskNotificationServiceTest {

    @InjectMocks
    private UnassignedTaskNotificationService unassignedTaskNotificationService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private VerifierAuthorityQueryService verifierAuthorityQueryService;

    @Mock
    private VerifierUserInfoService verifierUserInfoService;

    @Mock
    private WebAppProperties webAppProperties;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Test
    void sendUnassignedTaskNotificationService_whenVerifiersExist_sendEmailToAll() {
        Request request = new Request();
        request.setId("REQ-1");
        request.setAccountId(1L);
        request.setVerificationBodyId(10L);
        RequestTask requestTask = new RequestTask();
        requestTask.setType(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT);
        requestTask.setRequest(request);

        LegalEntity legalEntity = createLegalEntity("legalEntityName");
        Account account = createAccount(1L, "account", InstallationAccountStatus.LIVE, CompetentAuthorityEnum.ENGLAND, legalEntity);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        List<String> verifierAdmins = List.of("ver_admin@test.com");

        when(verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(10L)).thenReturn(verifierAdmins);

        List<UserInfoDTO> verifierUsersInfo = List.of(
                UserInfoDTO.builder().email("user1@test.com").build(),
                UserInfoDTO.builder().email("user2@test.com").build());

        when(verifierUserInfoService.getVerifierUsersInfo(verifierAdmins)).thenReturn(verifierUsersInfo);

        unassignedTaskNotificationService.sendUnassignedTaskNotificationService(requestTask);

        verify(notificationEmailService).notifyRecipient(any(), eq("user1@test.com"));
        verify(notificationEmailService).notifyRecipient(any(), eq("user2@test.com"));
        verify(notificationEmailService, times(2)).notifyRecipient(any(), anyString());
    }

    private LegalEntity createLegalEntity(String name) {
        return LegalEntity.builder()
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
    }

    private InstallationAccount createAccount(Long id, String name, InstallationAccountStatus status,
                                              CompetentAuthorityEnum competentAuthority, LegalEntity le) {

        return InstallationAccount.builder()
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
    }
}