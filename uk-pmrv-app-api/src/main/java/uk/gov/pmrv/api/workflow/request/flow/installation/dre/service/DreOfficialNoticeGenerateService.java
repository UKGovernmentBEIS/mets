package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DreOfficialNoticeGenerateService {

	private static final String FILE_NAME = "DRE_notice.pdf";

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

        final FileInfoDTO officialNotice = doGenerateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.DRE_SUBMIT,
                DocumentTemplateType.DRE_SUBMITTED,
				FILE_NAME);

        requestPayload.setOfficialNotice(officialNotice);
    }

	@Transactional
	public FileDTO doGenerateOfficialNoticeWithoutSave(final Request request, DecisionNotification decisionNotification) {

		final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
				.orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
		final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(decisionNotification);

		final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
				.constructTemplateParams(DocumentTemplateParamsSourceData.builder()
						.contextActionType(DocumentTemplateGenerationContextActionType.DRE_SUBMIT)
						.request(request)
						.signatory(decisionNotification.getSignatory())
						.accountPrimaryContact(accountPrimaryContact)
						.toRecipientEmail(accountPrimaryContact.getEmail())
						.ccRecipientsEmails(ccRecipientsEmails)
						.build());

		roundingTotalReportableEmissions(templateParams);
		return documentFileGeneratorService.generateFileDocument(DocumentTemplateType.DRE_SUBMITTED, templateParams, FILE_NAME);
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
		roundingTotalReportableEmissions(templateParams);
		return documentFileGeneratorService.generateAndSaveFileDocument(documentTemplateType, templateParams,
				fileNameToGenerate);
	}

	private void roundingTotalReportableEmissions(TemplateParams templateParams){
		String totalKey = "totalReportableEmissions";
		BigDecimal originalValue = (BigDecimal)templateParams.getParams().get(totalKey);
		if (originalValue != null) {
			BigDecimal roundedValue = originalValue.setScale(0, RoundingMode.HALF_UP);
			templateParams.getParams().put(totalKey, roundedValue);
		}
	}
	
}
