package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationDreOfficialNoticeGenerateService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    @Transactional
    public void generateOfficialNotice(String requestId) {
        Request request = requestService.findRequestById(requestId);
        AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();

        Optional<UserInfoDTO> accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
            .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.AVIATION_DRE_SUBMIT)
                .request(request)
                .signatory(requestPayload.getDecisionNotification().getSignatory())
                .accountPrimaryContact(accountPrimaryContact.orElse(null))
                .toRecipientEmail(accountPrimaryContact.map(UserInfoDTO::getEmail).orElse(null))
                .ccRecipientsEmails(ccRecipientsEmails)
                .build());

        FileInfoDTO officialNotice = documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.AVIATION_DRE_SUBMITTED,
            templateParams,
            "DRE_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }
}
