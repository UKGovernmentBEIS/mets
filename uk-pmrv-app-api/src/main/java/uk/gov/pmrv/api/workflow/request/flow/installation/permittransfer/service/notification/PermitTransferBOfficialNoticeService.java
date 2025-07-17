package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PermitTransferBOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final RequestService requestService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    @Transactional
    public CompletableFuture<FileInfoDTO> generateGrantedOfficialNotice(final String requestId) {
        
    	final Request request = requestService.findRequestById(requestId);
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        
        final String fileName = "permit_transfer_notice.pdf";
        
        return generateOfficialNoticeAsync(request,
            accountPrimaryContact,
            ccRecipientsEmails,
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_B_ACCEPTED,
            DocumentTemplateType.PERMIT_TRANSFER_ACCEPTED,
            fileName);
    }
    
    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_B_REFUSED,
                DocumentTemplateType.PERMIT_TRANSFER_REFUSED,
                "permit_transfer_refused_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_B_DEEMED_WITHDRAWN,
                DocumentTemplateType.PERMIT_TRANSFER_DEEMED_WITHDRAWN,
                "permit_transfer_deemed_withdrawn_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    public void sendOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
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

        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();

        final TemplateParams templateParams = constructTemplateParams(request, accountPrimaryContact, ccRecipientsEmails, type, requestPayload);
        return documentFileGeneratorService.generateAndSaveFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
    
    private CompletableFuture<FileInfoDTO> generateOfficialNoticeAsync(final Request request,
                                                                       final UserInfoDTO accountPrimaryContact,
                                                                       final List<String> ccRecipientsEmails,
                                                                       final DocumentTemplateGenerationContextActionType type,
                                                                       final DocumentTemplateType documentTemplateType,
                                                                       final String fileNameToGenerate) {
        
		final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();

		final TemplateParams templateParams = constructTemplateParams(request, accountPrimaryContact, ccRecipientsEmails, type, requestPayload);
		return documentFileGeneratorService.generateAndSaveFileDocumentAsync(documentTemplateType, templateParams, fileNameToGenerate);
	}

	private TemplateParams constructTemplateParams(final Request request, 
                                                   final UserInfoDTO accountPrimaryContact,
			                                       final List<String> ccRecipientsEmails, 
                                                   final DocumentTemplateGenerationContextActionType type,
			                                       final PermitTransferBRequestPayload requestPayload) {
        
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
