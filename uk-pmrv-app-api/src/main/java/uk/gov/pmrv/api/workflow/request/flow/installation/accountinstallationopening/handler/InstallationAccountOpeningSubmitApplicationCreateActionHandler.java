package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountCreationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper.InstallationAccountPayloadMapper;

import java.util.Map;

import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_RECEIVED;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningSubmitApplicationCreateActionHandler 
    implements RequestCreateActionHandler<InstallationAccountOpeningSubmitApplicationCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final NotificationEmailService notificationEmailService;
    private final RequestService requestService;
    private final InstallationAccountCreationService installationAccountCreationService;
    private final NotificationProperties notificationProperties;
    private static final InstallationAccountPayloadMapper INSTALLATION_ACCOUNT_PAYLOAD_MAPPER = Mappers.getMapper(InstallationAccountPayloadMapper.class);

    @Override
    public String process(final Long accountId,
            final RequestCreateActionType type,
            final InstallationAccountOpeningSubmitApplicationCreateActionPayload payload, 
            final PmrvUser pmrvUser) {
        final InstallationAccountPayload accountPayload = payload.getAccountPayload();

        InstallationAccountDTO accountDTO = INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toAccountInstallationDTO(accountPayload);
        
        //create account
        accountDTO = installationAccountCreationService.createAccount(accountDTO, pmrvUser);
        
        // enhance account payload with full legal entity info if id only provided
        if(accountPayload.getLegalEntity().getId() != null) {
            accountPayload.setLegalEntity(accountDTO.getLegalEntity());
        }

        //create request and start flow
        final boolean isTransfer = ApplicationType.TRANSFER.equals(accountPayload.getApplicationType());
        Request request = startProcessRequestService.startProcess(
            RequestParams.builder()
                .type(INSTALLATION_ACCOUNT_OPENING)
                .accountId(accountDTO.getId())
                .requestPayload(INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toInstallationAccountOpeningRequestPayload(accountPayload, pmrvUser.getUserId()))
                .processVars(Map.of(BpmnProcessConstants.APPLICATION_TYPE_IS_TRANSFER, isTransfer))
                .build()
        );
        
        //set request's submission date
        request.setSubmissionDate(request.getCreationDate());

        //create request action
        requestService.addActionToRequest(
            request,
            INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toInstallationAccountOpeningApplicationSubmittedRequestActionPayload(accountPayload),
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            pmrvUser.getUserId());

        //notify recipient
        notificationEmailService.notifyRecipient(
                EmailData.builder()
                    .notificationTemplateData(EmailNotificationTemplateData.builder()
                            .templateName(ACCOUNT_APPLICATION_RECEIVED)
                            .templateParams(Map.of(EmailNotificationTemplateConstants.CONTACT_REGULATOR,
                                    notificationProperties.getEmail().getContactUsLink()))
                            .build())
                    .build(),
                pmrvUser.getEmail());
        
        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION;
    }

}
