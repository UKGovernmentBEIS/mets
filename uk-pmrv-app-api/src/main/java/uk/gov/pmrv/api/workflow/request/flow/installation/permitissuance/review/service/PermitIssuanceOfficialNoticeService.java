package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitIssuanceOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final RequestService requestService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    @Transactional
    public CompletableFuture<FileInfoDTO> generateGrantedOfficialNotice(final String requestId) {
    	final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final boolean isGhge = requestPayload.getPermitType() == PermitType.GHGE;
        final DocumentTemplateGenerationContextActionType documentTemplateGenerationContextActionType = isGhge ?
                DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_GHGE_GRANTED :
                DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_HSE_GRANTED;
        
        final DocumentTemplateType documentTemplateType = isGhge ?
                DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED :
                DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED;
        
        final String fileName = isGhge ? "GHGE_permit_application_approved.pdf" : "HSE_permit_application_approved.pdf";
        
        return generateOfficialNoticeAsync(request,
            accountPrimaryContact,
            ccRecipientsEmails,
            documentTemplateGenerationContextActionType,
            documentTemplateType,
            fileName);
    }
    
    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_REJECTED,
                DocumentTemplateType.PERMIT_ISSUANCE_REJECTED,
                "permit_application_rejection_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.PERMIT_ISSUANCE_DEEMED_WITHDRAWN,
                DocumentTemplateType.PERMIT_ISSUANCE_DEEMED_WITHDRAWN,
                "permit_application_deemed_withdrawn_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    public void sendOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final List<FileInfoDTO> attachments = 
            requestPayload.getPermitDocument() != null ?
            List.of(requestPayload.getOfficialNotice(), requestPayload.getPermitDocument()) :
            List.of(requestPayload.getOfficialNotice());
        officialNoticeSendService.sendOfficialNotice(attachments, request, ccRecipientsEmails);
    }
    
    private FileInfoDTO generateOfficialNotice(final Request request,
                                               final UserInfoDTO accountPrimaryContact,
                                               final List<String> ccRecipientsEmails,
                                               final DocumentTemplateGenerationContextActionType type,
                                               final DocumentTemplateType documentTemplateType,
                                               final String fileNameToGenerate) {

        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();

        final TemplateParams templateParams = constructTemplateParams(request, accountPrimaryContact,
				ccRecipientsEmails, type, requestPayload);
        return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
    
    private CompletableFuture<FileInfoDTO> generateOfficialNoticeAsync(final Request request,
            final UserInfoDTO accountPrimaryContact,
            final List<String> ccRecipientsEmails,
            final DocumentTemplateGenerationContextActionType type,
            final DocumentTemplateType documentTemplateType,
            final String fileNameToGenerate) {
		final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();

		final TemplateParams templateParams = constructTemplateParams(request, accountPrimaryContact,
				ccRecipientsEmails, type, requestPayload);
		return documentFileGeneratorService.generateFileDocumentAsync(documentTemplateType, templateParams,
				fileNameToGenerate);
	}

	private TemplateParams constructTemplateParams(final Request request, final UserInfoDTO accountPrimaryContact,
			final List<String> ccRecipientsEmails, final DocumentTemplateGenerationContextActionType type,
			final PermitIssuanceRequestPayload requestPayload) {
		return documentTemplateOfficialNoticeParamsProvider
				.constructTemplateParams(DocumentTemplateParamsSourceData.builder()
						.contextActionType(type)
						.request(request)
						.signatory(requestPayload.getDecisionNotification().getSignatory())
						.accountPrimaryContact(accountPrimaryContact)
						.toRecipientEmail(accountPrimaryContact.getEmail())
						.ccRecipientsEmails(ccRecipientsEmails).build());
	}
}
