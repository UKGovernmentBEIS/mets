package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.config.RegistryConfig;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmpIssuanceOfficialNoticeService {

    private final RequestService requestService;

    private final RequestAccountContactQueryService requestAccountContactQueryService;

    private final DecisionNotificationUsersService decisionNotificationUsersService;

    private final DocumentFileGeneratorService documentFileGeneratorService;

    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    private final OfficialNoticeSendService officialNoticeSendService;
    private final RegistryConfig registryConfig;

    @Transactional
    public CompletableFuture<FileInfoDTO> generateGrantedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final UserInfoDTO serviceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        return generateOfficialNoticeAsync(request,
                accountPrimaryContact,
                serviceContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.EMP_ISSUANCE_GRANTED,
                DocumentTemplateType.EMP_ISSUANCE_UKETS_GRANTED,
                "emp_application_approved.pdf");
    }

    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final UserInfoDTO serviceContact = requestAccountContactQueryService.getRequestAccountServiceContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
            accountPrimaryContact,
            serviceContact,
            ccRecipientsEmails,
            DocumentTemplateGenerationContextActionType.EMP_ISSUANCE_DEEMED_WITHDRAWN,
            DocumentTemplateType.EMP_ISSUANCE_UKETS_DEEMED_WITHDRAWN,
            "emp_application_withdrawn.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    public void sendOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        final List<FileInfoDTO> attachments =
            requestPayload.getEmpDocument() != null ?
                List.of(requestPayload.getOfficialNotice(), requestPayload.getEmpDocument()) :
                List.of(requestPayload.getOfficialNotice());

        final List<String> ccRecipientsEmails = new ArrayList<>();
        ccRecipientsEmails.add(registryConfig.getEmail());
        ccRecipientsEmails.addAll(decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification()));

        officialNoticeSendService.sendOfficialNotice(attachments, request, ccRecipientsEmails);
    }

    private CompletableFuture<FileInfoDTO> generateOfficialNoticeAsync(final Request request,
                                                                       final UserInfoDTO accountPrimaryContact,
                                                                       final UserInfoDTO serviceContact,
                                                                       final List<String> ccRecipientsEmails,
                                                                       final DocumentTemplateGenerationContextActionType type,
                                                                       final DocumentTemplateType documentTemplateType,
                                                                       final String fileNameToGenerate) {
        final EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        final TemplateParams templateParams = constructTemplateParams(request, accountPrimaryContact,
                ccRecipientsEmails, type, requestPayload, serviceContact);
        return documentFileGeneratorService.generateFileDocumentAsync(documentTemplateType, templateParams,
                fileNameToGenerate);
    }

    private TemplateParams constructTemplateParams(final Request request, final UserInfoDTO accountPrimaryContact,
                                                   final List<String> ccRecipientsEmails, final DocumentTemplateGenerationContextActionType type,
                                                   final EmpIssuanceUkEtsRequestPayload requestPayload, final UserInfoDTO serviceContact) {
        return documentTemplateOfficialNoticeParamsProvider
                .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                        .contextActionType(type)
                        .request(request)
                        .signatory(requestPayload.getDecisionNotification().getSignatory())
                        .accountPrimaryContact(accountPrimaryContact)
                        .toRecipientEmail(serviceContact.getEmail())
                        .ccRecipientsEmails(ccRecipientsEmails).build());
    }

    private FileInfoDTO generateOfficialNotice(final Request request,
                                               final UserInfoDTO accountPrimaryContact,
                                               final UserInfoDTO serviceContact,
                                               final List<String> ccRecipientsEmails,
                                               final DocumentTemplateGenerationContextActionType type,
                                               final DocumentTemplateType documentTemplateType,
                                               final String fileNameToGenerate) {

        final EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        final TemplateParams templateParams = constructTemplateParams(request, accountPrimaryContact,
                ccRecipientsEmails, type, requestPayload, serviceContact);
        return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }
}
