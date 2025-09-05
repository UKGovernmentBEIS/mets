package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HSETIOfficialNoticeService {


    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;


    @Transactional
    public void generateOfficialNoticeApproved(String requestId) {
        String fileNameApproved = "HSE_target_increase_application_approved.pdf";
        generateOfficialNotice(requestId, fileNameApproved);
    }

    @Transactional
    public void generateOfficialNoticeRejected(String requestId) {
        String fileNameRejected = "HSE_target_increase_application_rejected.pdf";
        generateOfficialNotice(requestId, fileNameRejected);
    }

    @Transactional
    public void generateOfficialNoticeWithdrawn(String requestId) {
        String fileNameWithdrawn = "HSE_target_increase_application_withdrawn.pdf";
        generateOfficialNotice(requestId, fileNameWithdrawn);
    }

    @Transactional
    public void generateOfficialNoticeDeemedWithdrawn(String requestId) {
        String fileNameDeemedWithdrawn = "HSE_target_increase_application_deemed_withdrawn.pdf";
        generateOfficialNotice(requestId, fileNameDeemedWithdrawn);
    }


    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final HSETIRequestPayload requestPayload =
                (HSETIRequestPayload) request.getPayload();
        final List<String> decisionRecipients = decisionNotificationUsersService
                .findUserEmails(requestPayload.getDecisionNotification());

        officialNoticeSendService.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request,
                decisionRecipients);
    }


    private void generateOfficialNotice(String requestId, String fileName) {

        final Request request = requestService.findRequestById(requestId);
        final HSETIRequestPayload requestPayload =
                (HSETIRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails =
                decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
                .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.HSE_TI_COMPLETED)
                        .request(request)
                        .signatory(requestPayload.getDecisionNotification().getSignatory())
                        .accountPrimaryContact(accountPrimaryContact)
                        .toRecipientEmail(accountPrimaryContact.getEmail())
                        .ccRecipientsEmails(ccRecipientsEmails).build());


        final FileInfoDTO officialNotice = documentFileGeneratorService
                .generateAndSaveFileDocument(DocumentTemplateType.HSE_TI_COMPLETED, templateParams, fileName);

        requestPayload.setOfficialNotice(officialNotice);
    }
}
