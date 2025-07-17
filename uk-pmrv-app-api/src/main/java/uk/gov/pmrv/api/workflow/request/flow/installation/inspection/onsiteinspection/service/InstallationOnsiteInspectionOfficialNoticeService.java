package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionOfficialNoticeService;

import java.util.List;

@Service
public class InstallationOnsiteInspectionOfficialNoticeService extends InstallationInspectionOfficialNoticeService {

    private final String fileName = "INSTALLATION_ONSITE_INSPECTION_notice.pdf";

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;

    public InstallationOnsiteInspectionOfficialNoticeService(RequestService requestService,
                                                  RequestAccountContactQueryService requestAccountContactQueryService,
                                                  DecisionNotificationUsersService decisionNotificationUsersService,
                                                  DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider,
                                                  DocumentFileGeneratorService documentFileGeneratorService,
                                                  OfficialNoticeSendService officialNoticeSendService) {
        super(requestService,
                decisionNotificationUsersService,
                documentTemplateOfficialNoticeParamsProvider,
                documentFileGeneratorService,
                officialNoticeSendService);

        this.requestService = requestService;
        this.requestAccountContactQueryService = requestAccountContactQueryService;
        this.decisionNotificationUsersService = decisionNotificationUsersService;
    }


    @Transactional
    public void generateInstallationOnsiteInspectionSubmittedOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails =
                decisionNotificationUsersService.findUserEmails(requestPayload.getDecisionNotification());

        final FileInfoDTO officialNotice = doGenerateOfficialNotice(request,
                accountPrimaryContact,
                ccRecipientsEmails,
                DocumentTemplateGenerationContextActionType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED,
                DocumentTemplateType.INSTALLATION_ONSITE_INSPECTION_SUBMITTED,
                fileName);

        requestPayload.setOfficialNotice(officialNotice);
    }
}