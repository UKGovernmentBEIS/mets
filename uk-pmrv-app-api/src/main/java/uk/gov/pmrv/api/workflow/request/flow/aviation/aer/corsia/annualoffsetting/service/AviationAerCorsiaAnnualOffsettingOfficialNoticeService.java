package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.util.List;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;


@Service
public class AviationAerCorsiaAnnualOffsettingOfficialNoticeService {

    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final String fileName = "AVIATION_AER_ANNUAL_OFFSETTING_notice.pdf";

    public AviationAerCorsiaAnnualOffsettingOfficialNoticeService(
            DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider,
            DocumentFileGeneratorService documentFileGeneratorService,
            RequestService requestService,
            RequestAccountContactQueryService requestAccountContactQueryService,
            DecisionNotificationUsersService decisionNotificationUsersService,
            OfficialNoticeSendService officialNoticeSendService) {
        this.documentTemplateOfficialNoticeParamsProvider = documentTemplateOfficialNoticeParamsProvider;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.requestService = requestService;
        this.requestAccountContactQueryService = requestAccountContactQueryService;
        this.decisionNotificationUsersService = decisionNotificationUsersService;
        this.officialNoticeSendService = officialNoticeSendService;
    }

    @Transactional
    public void generateAviationAerCorsiaAnnualOffsettingSubmittedOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload =
                (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails =
                decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = doGenerateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED,
                DocumentTemplateType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED,
                fileName);

        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public FileDTO doGenerateOfficialNoticeWithoutSave(final Request request,
                                                       DecisionNotification decisionNotification) {

        final UserInfoDTO accountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(decisionNotification);

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
                    .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                            .contextActionType(
                                    DocumentTemplateGenerationContextActionType
                                            .AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED)
                            .request(request)
                            .signatory(decisionNotification.getSignatory())
                            .accountPrimaryContact(accountPrimaryContact)
                            .toRecipientEmail(accountPrimaryContact.getEmail())
                            .ccRecipientsEmails(ccRecipientsEmails)
                            .build());

        return documentFileGeneratorService
                .generateFileDocument(
                        DocumentTemplateType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED,
                        templateParams,
                        fileName);

    }

    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload =
                (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();
        final List<String> decisionRecipients = decisionNotificationUsersService
                .findUserEmails(requestPayload.getDecisionNotification());

        officialNoticeSendService.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request,
                decisionRecipients);
    }

    private FileInfoDTO doGenerateOfficialNotice(final Request request,
                                                     final UserInfoDTO accountPrimaryContact,
                                                     final List<String> ccRecipientsEmails,
                                                     final DocumentTemplateGenerationContextActionType type,
                                                     final DocumentTemplateType documentTemplateType,
                                                     final String fileNameToGenerate) {
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload =
                (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
                .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                        .contextActionType(type)
                        .request(request)
                        .signatory(requestPayload.getDecisionNotification().getSignatory())
                        .accountPrimaryContact(accountPrimaryContact)
                        .toRecipientEmail(accountPrimaryContact.getEmail())
                        .ccRecipientsEmails(ccRecipientsEmails).build());

        return documentFileGeneratorService.generateAndSaveFileDocument(documentTemplateType, templateParams,
                fileNameToGenerate);
    }



}
