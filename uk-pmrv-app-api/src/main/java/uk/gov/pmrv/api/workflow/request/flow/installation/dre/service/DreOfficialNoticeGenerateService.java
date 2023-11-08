package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import java.util.List;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@Service
@RequiredArgsConstructor
public class DreOfficialNoticeGenerateService {

	private final RequestService requestService;
	private final RequestAccountContactQueryService requestAccountContactQueryService;
	private final DecisionNotificationUsersService decisionNotificationUsersService;
	private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
	private final DocumentFileGeneratorService documentFileGeneratorService;
	
	@Transactional
    public void generateDreSubmittedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
			.orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final String fileName = "DRE_notice.pdf";
        
        final FileInfoDTO officialNotice = doGenerateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.DRE_SUBMIT,
                DocumentTemplateType.DRE_SUBMITTED,
                fileName);

        requestPayload.setOfficialNotice(officialNotice);
    }
	
	private FileInfoDTO doGenerateOfficialNotice(final Request request, final UserInfoDTO accountPrimaryContact,
			final List<String> ccRecipientsEmails, final DocumentTemplateGenerationContextActionType type,
			final DocumentTemplateType documentTemplateType, final String fileNameToGenerate) {
		final DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();
		
		final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
				.constructTemplateParams(DocumentTemplateParamsSourceData.builder()
						.contextActionType(type)
						.request(request)
						.signatory(requestPayload.getDecisionNotification().getSignatory())
						.accountPrimaryContact(accountPrimaryContact)
						.toRecipientEmail(accountPrimaryContact.getEmail())
						.ccRecipientsEmails(ccRecipientsEmails).build());
				
		return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams,
				fileNameToGenerate);
	}
	
}
