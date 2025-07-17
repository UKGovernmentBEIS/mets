package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper.InstallationInspectionMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private static final InstallationInspectionMapper INSPECTION_MAPPER =
            Mappers.getMapper(InstallationInspectionMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationOnsiteInspectionRequestPayload requestPayload =
                (InstallationOnsiteInspectionRequestPayload) request.getPayload();
        final InstallationInspectionApplicationSubmitRequestTaskPayload taskPayload;

        if (installationOnsiteInspectionExists(requestPayload)) {
            taskPayload = INSPECTION_MAPPER.toInstallationInspectionApplicationSubmitRequestTaskPayload(requestPayload,
                    RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD);
        } else {
            taskPayload = InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD)
                    .build();
        }
        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT);
    }

    private boolean installationOnsiteInspectionExists(InstallationOnsiteInspectionRequestPayload requestPayload) {
        return requestPayload.getInstallationInspection() != null;
    }
}
