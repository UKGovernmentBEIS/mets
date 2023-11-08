package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoalOfficialNoticeService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    @Transactional
    public void generateAndSaveProceededToAuthorityOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        setOfficialNoticeAndRecipients(request,
                DocumentTemplateGenerationContextActionType.DOAL_SUBMITTED,
                DocumentTemplateType.DOAL_SUBMITTED,
                "Activity_level_determination_preliminary_allocation_letter.pdf");
    }

    @Transactional
    public void generateAndSaveAuthorityResponseOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload payload = (DoalRequestPayload) request.getPayload();

        if(payload.getDoalAuthority().getAuthorityResponse().getType().equals(DoalAuthorityResponseType.INVALID)) {
            setOfficialNoticeAndRecipients(request,
                    DocumentTemplateGenerationContextActionType.DOAL_REJECTED,
                    DocumentTemplateType.DOAL_REJECTED,
                    "Activity_level_determination_not_approved_by_Authority_notice.pdf");
        }
        else {
            setOfficialNoticeAndRecipients(request,
                    DocumentTemplateGenerationContextActionType.DOAL_ACCEPTED,
                    DocumentTemplateType.DOAL_ACCEPTED,
                    "Activity_level_determination_approved_by_Authority_notice.pdf");
        }
    }

    public void sendOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final List<FileInfoDTO> attachments = List.of(requestPayload.getOfficialNotice());

        officialNoticeSendService.sendOfficialNotice(attachments, request, ccRecipientsEmails);
    }

    private void setOfficialNoticeAndRecipients(Request request,
                                   final DocumentTemplateGenerationContextActionType contextActionType,
                                   final DocumentTemplateType templateType,
                                   final String fileName) {
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        // Generate file
        final FileInfoDTO officialNotice = generateOfficialNotice(request, accountPrimaryContact,
                ccRecipientsEmails, contextActionType, templateType, fileName);

        // Save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }

    private FileInfoDTO generateOfficialNotice(final Request request,
                                               final UserInfoDTO accountPrimaryContact,
                                               final List<String> ccRecipientsEmails,
                                               final DocumentTemplateGenerationContextActionType contextActionType,
                                               final DocumentTemplateType templateType,
                                               final String fileName) {

        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(
                DocumentTemplateParamsSourceData.builder()
                        .contextActionType(contextActionType)
                        .request(request)
                        .signatory(requestPayload.getDecisionNotification().getSignatory())
                        .accountPrimaryContact(accountPrimaryContact)
                        .toRecipientEmail(accountPrimaryContact.getEmail())
                        .ccRecipientsEmails(ccRecipientsEmails)
                        .build()
        );
        return documentFileGeneratorService.generateFileDocument(templateType, templateParams, fileName);
    }
}
