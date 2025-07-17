package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import java.util.List;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

@Service
@RequiredArgsConstructor
public class AviationVirOfficialNoticeService {

    private static final String FILE_NAME = "Recommended_improvements.pdf";

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    @Transactional
    public void generateAndSaveRecommendedImprovementsOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationVirRequestPayload requestPayload = (AviationVirRequestPayload) request.getPayload();
        // Generate file
        final FileInfoDTO officialNotice = generateOfficialNotice(request);
        // Save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }

    public void sendOfficialNotice(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final AviationVirRequestPayload requestPayload = (AviationVirRequestPayload) request.getPayload();
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());
        final List<FileInfoDTO> attachments = List.of(requestPayload.getOfficialNotice());

        officialNoticeSendService.sendOfficialNotice(attachments, request, ccRecipientsEmails);
    }

    @Transactional
    public FileDTO doGenerateOfficialNoticeWithoutSave(Request dbRequest) {
        final TemplateParams templateParams = constructTemplateParams(dbRequest);
        return documentFileGeneratorService.generateFileDocument(DocumentTemplateType.AVIATION_VIR_REVIEWED,
                templateParams, FILE_NAME);
    }

    private FileInfoDTO generateOfficialNotice(final Request request) {
        final TemplateParams templateParams = constructTemplateParams(request);
        return documentFileGeneratorService.generateAndSaveFileDocument(DocumentTemplateType.AVIATION_VIR_REVIEWED,
                templateParams, FILE_NAME);
    }

    private TemplateParams constructTemplateParams(final Request request) {

        final AviationVirRequestPayload requestPayload = (AviationVirRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        return documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(
                 DocumentTemplateParamsSourceData.builder()
                        .contextActionType(DocumentTemplateGenerationContextActionType.AVIATION_VIR_REVIEWED)
                        .request(request)
                        .signatory(requestPayload.getDecisionNotification().getSignatory())
                        .accountPrimaryContact(accountPrimaryContact)
                        .toRecipientEmail(accountPrimaryContact.getEmail())
                        .ccRecipientsEmails(ccRecipientsEmails)
                        .build()
        );
    }
}
