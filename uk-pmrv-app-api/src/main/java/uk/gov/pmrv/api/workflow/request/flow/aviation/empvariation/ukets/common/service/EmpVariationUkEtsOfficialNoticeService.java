package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsOfficialNoticeService {

	private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Transactional
    public CompletableFuture<FileInfoDTO> generateApprovedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final UserInfoDTO serviceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final EmissionsMonitoringPlanUkEtsDTO emp = emissionsMonitoringPlanQueryService
        		.getEmissionsMonitoringPlanUkEtsDTOByAccountId(request.getAccountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        requestPayload.setEmpConsolidationNumber(emp.getConsolidationNumber()); // TODO move it outside of this method

        return generateOfficialNoticeAsync(request,
                accountPrimaryContact,
                serviceContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.EMP_VARIATION_ACCEPTED,
                DocumentTemplateType.EMP_VARIATION_UKETS_ACCEPTED,
                "emp_variation_approved.pdf");
    }
    
    @Transactional
    public CompletableFuture<FileInfoDTO> generateApprovedOfficialNoticeRegulatorLed(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final UserInfoDTO serviceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final EmissionsMonitoringPlanUkEtsDTO emp = emissionsMonitoringPlanQueryService
        		.getEmissionsMonitoringPlanUkEtsDTOByAccountId(request.getAccountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        requestPayload.setEmpConsolidationNumber(emp.getConsolidationNumber()); // TODO move it outside of this method

        return generateOfficialNoticeAsync(request,
                accountPrimaryContact,
                serviceContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.EMP_VARIATION_REGULATOR_LED_APPROVED,
                DocumentTemplateType.EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED,
                "emp_ca_variation_approved.pdf");
    }
    
    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final UserInfoDTO serviceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = generateOfficialNotice(request,
                accountPrimaryContact,
                serviceContact,
                ccRecipientsEmails,
                null,
                DocumentTemplateType.EMP_VARIATION_UKETS_DEEMED_WITHDRAWN,
                "emp_variation_withdrawn.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }
    
    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final UserInfoDTO serviceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = generateOfficialNotice(request,
                accountPrimaryContact,
                serviceContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.EMP_VARIATION_REJECTED,
                DocumentTemplateType.EMP_VARIATION_UKETS_REJECTED,
                "emp_variation_rejected.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }
    
    public void sendOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final List<FileInfoDTO> attachments = requestPayload.getEmpDocument() != null ? 
                List.of(requestPayload.getOfficialNotice(), requestPayload.getEmpDocument()) :
                List.of(requestPayload.getOfficialNotice());
        officialNoticeSendService.sendOfficialNotice(attachments, request, ccRecipientsEmails);
    }
    
    private CompletableFuture<FileInfoDTO> generateOfficialNoticeAsync(final Request request,
            final UserInfoDTO accountPrimaryContact,
            final UserInfoDTO serviceContact, 
            final List<String> ccRecipientsEmails,
            final DocumentTemplateGenerationContextActionType type,
            final DocumentTemplateType documentTemplateType,
            final String fileNameToGenerate) {
		
		final TemplateParams templateParams = buildTemplateParams(request, accountPrimaryContact,
				serviceContact, ccRecipientsEmails, type);
		return documentFileGeneratorService.generateFileDocumentAsync(documentTemplateType, templateParams,
				fileNameToGenerate);
    }
    
    private FileInfoDTO generateOfficialNotice(final Request request, 
			final UserInfoDTO accountPrimaryContact,
			final UserInfoDTO serviceContact, 
			final List<String> ccRecipientsEmails, 
			final DocumentTemplateGenerationContextActionType type,
			final DocumentTemplateType documentTemplateType, 
			final String fileNameToGenerate) {
        final TemplateParams templateParams = buildTemplateParams(request, 
        		accountPrimaryContact, 
        		serviceContact,
        		ccRecipientsEmails,
				type);
        return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
    
    private TemplateParams buildTemplateParams(final Request request, final UserInfoDTO accountPrimaryContact,
            final UserInfoDTO serviceContact, final List<String> ccRecipientsEmails, 
            final DocumentTemplateGenerationContextActionType type) {
    	final EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
    	
    	return documentTemplateOfficialNoticeParamsProvider
			.constructTemplateParams(DocumentTemplateParamsSourceData.builder()
				.contextActionType(type)
				.request(request)
				.signatory(requestPayload.getDecisionNotification().getSignatory())
				.accountPrimaryContact(accountPrimaryContact)
				.toRecipientEmail(serviceContact.getEmail())
				.ccRecipientsEmails(ccRecipientsEmails).build());
		}
}
