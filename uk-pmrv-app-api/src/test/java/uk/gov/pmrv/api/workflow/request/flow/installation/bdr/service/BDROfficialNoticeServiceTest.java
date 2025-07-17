package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDROfficialNoticeServiceTest {

    @InjectMocks
    private BDROfficialNoticeService officialNoticeService;

    @Mock
    private RequestService requestService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
 	private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;


    @Test
    void sendOfficialNotice_accountPrimaryContactExists_sendEmail() {
        final String requestId = "BDR00001-2025";
        final Long accountId = 1L;
        final String primaryContactUserId = "userId";

        UserInfoDTO userInfo = UserInfoDTO.builder().userId(primaryContactUserId).email("topal@pmrv.uk").build();

        Request request = Request
                .builder()
                .type(RequestType.BDR)
                .id(requestId)
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountContactQueryService.findPrimaryContactByAccount(request.getAccountId())).thenReturn(Optional.of(primaryContactUserId));
        when(userAuthService.getUserByUserId(primaryContactUserId)).thenReturn(userInfo);

        final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
				.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
						.competentAuthority(request.getCompetentAuthority())
					    .templateName(PmrvNotificationTemplateName.BDR_COMPLETED.getName())
						.accountType(request.getType().getAccountType())
						.templateParams(Map.of())
						.build())
				.build();

        officialNoticeService.sendOfficialNotice(requestId);

        verify(	notificationEmailService, times(1)).notifyRecipient(emailData, userInfo.getEmail());
    }

    @Test
    void sendOfficialNotice_accountPrimaryContactDoesNotExist_throwException() {
        final String requestId = "BDR00001-2025";
        final Long accountId = 1L;

        Request request = Request
                .builder()
                .type(RequestType.BDR)
                .id(requestId)
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountContactQueryService.findPrimaryContactByAccount(request.getAccountId())).thenReturn(Optional.empty());


        BusinessException be = assertThrows(BusinessException.class, () ->
                        officialNoticeService.sendOfficialNotice(requestId));

		assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.BDR_PRIMARY_CONTACT_NOT_FOUND);

        verify(	notificationEmailService, times(0)).notifyRecipient(any(),any());
    }
}
