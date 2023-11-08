package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationOfficialNoticeService {

	private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final RequestService requestService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;
    
    @Transactional
    public CompletableFuture<FileInfoDTO> generateGrantedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        return generateOfficialNoticeAsync(request,
            accountPrimaryContact,
            ccRecipientsEmails,
            DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_GRANTED,
            DocumentTemplateType.PERMIT_VARIATION_ACCEPTED,
            "permit_variation_approved.pdf");
    }
    
    @Transactional
    public CompletableFuture<FileInfoDTO> generateRegulatorLedApprovedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        return generateOfficialNoticeAsync(request,
            accountPrimaryContact,
            ccRecipientsEmails,
            DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REGULATOR_LED_APPROVED,
            DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED,
            "permit_ca_variation_approved.pdf");
    }

    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = generateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.PERMIT_VARIATION_REJECTED,
                DocumentTemplateType.PERMIT_VARIATION_REJECTED,
                "permit_variation_rejected.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = generateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                null,
                DocumentTemplateType.PERMIT_VARIATION_DEEMED_WITHDRAWN,
                "permit_variation_deemed_withdrawn.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    public void sendOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final List<FileInfoDTO> attachments = requestPayload.getPermitDocument() != null ? 
                List.of(requestPayload.getOfficialNotice(), requestPayload.getPermitDocument()) :
                List.of(requestPayload.getOfficialNotice());
        officialNoticeSendService.sendOfficialNotice(attachments, request, ccRecipientsEmails);
    }
    
	private CompletableFuture<FileInfoDTO> generateOfficialNoticeAsync(final Request request,
			final UserInfoDTO accountPrimaryContact, 
			final List<String> ccRecipientsEmails,
			final DocumentTemplateGenerationContextActionType type, 
			final DocumentTemplateType documentTemplateType,
			final String fileNameToGenerate) {
		final TemplateParams templateParams = buildTemplateParams(request, 
				accountPrimaryContact, 
				ccRecipientsEmails,
				type);
		return documentFileGeneratorService.generateFileDocumentAsync(documentTemplateType, templateParams,
				fileNameToGenerate);
	}

	private FileInfoDTO generateOfficialNotice(final Request request, 
			final UserInfoDTO accountPrimaryContact,
			final List<String> ccRecipientsEmails, 
			final DocumentTemplateGenerationContextActionType type,
			final DocumentTemplateType documentTemplateType, 
			final String fileNameToGenerate) {
        final TemplateParams templateParams = buildTemplateParams(request, 
        		accountPrimaryContact, 
        		ccRecipientsEmails,
				type);
        return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
    
    private TemplateParams buildTemplateParams(final Request request, final UserInfoDTO accountPrimaryContact,
			final List<String> ccRecipientsEmails, final DocumentTemplateGenerationContextActionType type) {
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

		final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
				.constructTemplateParams(DocumentTemplateParamsSourceData.builder()
						.contextActionType(type)
						.request(request)
						.signatory(requestPayload.getDecisionNotification().getSignatory())
						.accountPrimaryContact(accountPrimaryContact)
						.toRecipientEmail(accountPrimaryContact.getEmail())
						.ccRecipientsEmails(ccRecipientsEmails).build());
		return templateParams;
	}
}
