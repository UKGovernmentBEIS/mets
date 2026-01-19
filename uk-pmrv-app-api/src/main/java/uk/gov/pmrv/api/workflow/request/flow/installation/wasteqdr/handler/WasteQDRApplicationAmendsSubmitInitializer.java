package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper.WasteQDRMapper;

import java.util.Set;

@Service
public class WasteQDRApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final WasteQDRMapper WASTE_QDR_MAPPER = Mappers.getMapper(WasteQDRMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return WASTE_QDR_MAPPER.toWasteQDRApplicationAmendsSubmitRequestTaskPayload(
                (WasteQDRRequestPayload) request.getPayload(), (WasteQDRRequestMetaData) request.getMetadata());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.WASTE_QDR_APPLICATION_AMENDS_SUBMIT);
    }
}
