package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestExpirationReminderServiceTest {

    @InjectMocks
    private RequestExpirationReminderService service;

    @Mock
    private RequestService requestService;
    
    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator;


    @Test
    void sendExpirationReminderNotification() {
        String requestId = "1";
        Long accountId = 1L;
        Date deadline = new Date();
        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).email("email").build();

        NotificationTemplateExpirationReminderParams expirationParams = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask("request for information")
                .recipient(UserInfoDTO.builder()
                        .email("recipient@email")
                        .firstName("fn").lastName("ln")
                        .build())
                .expirationTime("1 day")
                .expirationTimeLong("in one day")
                .deadline(deadline)
                .build();
        
        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        String emitterId = "emitterId";
        AccountInfoDTO account = AccountInfoDTO.builder()
                .id(accountId)
                .name("account name")
                .emitterId(emitterId)
                .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountQueryService.getAccountInfoDTOById(accountId)).thenReturn(account);
        when(competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, AccountType.INSTALLATION))
                .thenReturn(ca);

        //invoke
        service.sendExpirationReminderNotification(requestId, expirationParams);

        verify(competentAuthorityDTOByRequestResolverDelegator, times(1)).resolveCA(request, AccountType.INSTALLATION);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountQueryService, times(1)).getAccountInfoDTOById(accountId);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), Mockito.eq("recipient@email"));
        EmailData<PmrvEmailNotificationTemplateData> emailDataCaptured = emailDataCaptor.getValue();
        
        final Map<String, Object> expectedTemplateParams = new HashMap<>();
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, account.getName());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId);
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, request.getId());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW, request.getType().getDescription());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_TASK, expirationParams.getWorkflowTask());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_USER, expirationParams.getRecipient().getFullName());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME, expirationParams.getExpirationTime());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME_LONG, expirationParams.getExpirationTimeLong());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_DEADLINE, expirationParams.getDeadline());
        expectedTemplateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, ca.getEmail());
        
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .templateName(PmrvNotificationTemplateName.GENERIC_EXPIRATION_REMINDER.getName())
                        .accountType(AccountType.INSTALLATION)
                        .templateParams(expectedTemplateParams)
                        .build())
                .build());
        
    }
    
}
