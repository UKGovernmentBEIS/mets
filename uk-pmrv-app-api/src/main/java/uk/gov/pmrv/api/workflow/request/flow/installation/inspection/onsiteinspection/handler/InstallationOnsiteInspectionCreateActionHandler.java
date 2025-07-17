package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

@Component
@RequiredArgsConstructor
public class InstallationOnsiteInspectionCreateActionHandler
        implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final DateService dateService;

    @Override
    public String process(Long accountId,
                          RequestCreateActionEmptyPayload payload, AppUser appUser) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.INSTALLATION_ONSITE_INSPECTION)
                .accountId(accountId)
                .requestPayload(InstallationInspectionRequestPayload.builder()
                        .payloadType(RequestPayloadType.INSTALLATION_ONSITE_INSPECTION_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .installationInspection(InstallationInspection.builder().details(InstallationInspectionDetails.builder().build()).build())
                        .build())
                .requestMetadata(InstallationInspectionRequestMetadata.builder()
                        .type(RequestMetadataType.INSTALLATION_INSPECTION)
                        .year(dateService.getYear())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.INSTALLATION_ONSITE_INSPECTION;
    }
}
