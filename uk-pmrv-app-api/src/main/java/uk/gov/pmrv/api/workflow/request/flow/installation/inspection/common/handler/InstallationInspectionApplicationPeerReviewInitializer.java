package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.mapper.InstallationInspectionMapper;

@Service
@RequiredArgsConstructor
public abstract class InstallationInspectionApplicationPeerReviewInitializer
        implements InitializeRequestTaskHandler {

    private static final InstallationInspectionMapper INSTALLATION_INSPECTION_MAPPER = Mappers.getMapper(InstallationInspectionMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final InstallationInspectionRequestPayload requestPayload = (InstallationInspectionRequestPayload) request.getPayload();
        return INSTALLATION_INSPECTION_MAPPER.toInstallationInspectionApplicationSubmitRequestTaskPayload(requestPayload,
               this.getRequestTaskPayloadType());
    }


    protected abstract RequestTaskPayloadType getRequestTaskPayloadType();

}
