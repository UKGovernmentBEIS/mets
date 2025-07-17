package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BDROfficialNoticeService {

    private final RequestService requestService;
    private final UserAuthService userAuthService;
    private final AccountContactQueryService accountContactQueryService;
 	private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        Optional<String> primaryContactUserId =
                accountContactQueryService.findPrimaryContactByAccount(request.getAccountId());

        if (primaryContactUserId.isEmpty()) {
            throw new BusinessException(MetsErrorCode.BDR_PRIMARY_CONTACT_NOT_FOUND);
        }

        UserInfoDTO userInfo = userAuthService.getUserByUserId(primaryContactUserId.get());

        final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
				.notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
						.competentAuthority(request.getCompetentAuthority())
					    .templateName(PmrvNotificationTemplateName.BDR_COMPLETED.getName())
						.accountType(request.getType().getAccountType())
						.templateParams(Map.of())
						.build())
				.build();

		notificationEmailService.notifyRecipient(emailData, userInfo.getEmail());
    }
}

