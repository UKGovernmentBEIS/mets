package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper.InstallationInspectionMapper;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class InstallationAuditApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private static final InstallationInspectionMapper INSPECTION_MAPPER =
            Mappers.getMapper(InstallationInspectionMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationAuditRequestPayload requestPayload = (InstallationAuditRequestPayload) request.getPayload();
        final InstallationInspectionApplicationSubmitRequestTaskPayload taskPayload;

        if (installationAuditExists(requestPayload)) {
            taskPayload = INSPECTION_MAPPER.toInstallationInspectionApplicationSubmitRequestTaskPayload(requestPayload,
                    RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_SUBMIT_PAYLOAD);
        } else {
            taskPayload = InstallationAuditApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_SUBMIT_PAYLOAD)
                    .build();
        }
        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_AUDIT_APPLICATION_SUBMIT);
    }

    private boolean installationAuditExists(InstallationAuditRequestPayload requestPayload) {
        return requestPayload.getInstallationInspection() != null;
    }
}
