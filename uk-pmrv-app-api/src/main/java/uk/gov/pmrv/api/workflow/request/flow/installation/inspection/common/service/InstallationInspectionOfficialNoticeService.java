package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

import java.util.List;


public class InstallationInspectionOfficialNoticeService {

    private final RequestService requestService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    protected InstallationInspectionOfficialNoticeService(RequestService requestService,
                                                          DecisionNotificationUsersService
                                                                  decisionNotificationUsersService,
                                                          DocumentTemplateOfficialNoticeParamsProvider
                                                                  documentTemplateOfficialNoticeParamsProvider,
                                                          DocumentFileGeneratorService documentFileGeneratorService,
                                                          OfficialNoticeSendService officialNoticeSendService) {
        this.requestService = requestService;
        this.decisionNotificationUsersService = decisionNotificationUsersService;
        this.documentTemplateOfficialNoticeParamsProvider = documentTemplateOfficialNoticeParamsProvider;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.officialNoticeSendService = officialNoticeSendService;
    }

    protected FileInfoDTO doGenerateOfficialNotice(final Request request,
                                                 final UserInfoDTO accountPrimaryContact,
                                                 final List<String> ccRecipientsEmails,
                                                 final DocumentTemplateGenerationContextActionType type,
                                                 final DocumentTemplateType documentTemplateType,
                                                 final String fileNameToGenerate) {
        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) request.getPayload();

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


    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) request.getPayload();
        final List<String> decisionRecipients = decisionNotificationUsersService
                .findUserEmails(requestPayload.getDecisionNotification());

        officialNoticeSendService.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request,
                decisionRecipients);
    }
}
