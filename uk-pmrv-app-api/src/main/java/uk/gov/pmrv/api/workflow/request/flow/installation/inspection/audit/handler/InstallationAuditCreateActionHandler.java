package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestMetadata;

@Component
@RequiredArgsConstructor
public class InstallationAuditCreateActionHandler
        implements RequestAccountCreateActionHandler<InstallationAuditRequestCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId,
                          InstallationAuditRequestCreateActionPayload payload, AppUser appUser) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.INSTALLATION_AUDIT)
                .accountId(accountId)
                .requestPayload(InstallationAuditRequestPayload.builder()
                        .payloadType(RequestPayloadType.INSTALLATION_AUDIT_REQUEST_PAYLOAD)
                        .auditYear(payload.getYear())
                        .regulatorAssignee(appUser.getUserId())
                        .installationInspection(InstallationInspection.builder().details(InstallationInspectionDetails.builder().build()).build())
                        .build())
                .requestMetadata(InstallationInspectionRequestMetadata.builder()
                        .type(RequestMetadataType.INSTALLATION_INSPECTION)
                        .year(payload.getYear())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.INSTALLATION_AUDIT;
    }
}
