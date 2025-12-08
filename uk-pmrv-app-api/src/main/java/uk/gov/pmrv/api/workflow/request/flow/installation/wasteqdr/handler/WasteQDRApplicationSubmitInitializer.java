package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class WasteQDRApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final WasteQDRRequestPayload requestPayload =
                (WasteQDRRequestPayload) request.getPayload();

        final WasteQDRApplicationSubmitRequestTaskPayload taskPayload;

        taskPayload = WasteQDRApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.WASTE_QDR_SUBMIT_PAYLOAD)
                .qdr(requestPayload.getQdr())
                .build();

        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.WASTE_QDR_APPLICATION_SUBMIT);
    }
}
