package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;

@Service
@RequiredArgsConstructor
public class AirApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final Map<Integer, AirImprovement> airImprovements = ((AirRequestPayload) request.getPayload()).getAirImprovements();

        return AirApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_APPLICATION_SUBMIT_PAYLOAD)
            .airImprovements(airImprovements)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AIR_APPLICATION_SUBMIT);
    }
}
