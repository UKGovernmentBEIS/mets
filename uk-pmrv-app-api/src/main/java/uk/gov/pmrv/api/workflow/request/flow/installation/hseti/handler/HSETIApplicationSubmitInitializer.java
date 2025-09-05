package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HSETIApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final HSETIRequestPayload requestPayload =
                (HSETIRequestPayload) request.getPayload();

        final HSETIApplicationSubmitRequestTaskPayload taskPayload = HSETIApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.HSE_TI_APPLICATION_SUBMIT_PAYLOAD)
                    .hseti(requestPayload.getHseti())
                    .hsetiAttachments(requestPayload.getHsetiAttachments())
                    .hsetiSectionsCompleted(requestPayload.getHsetiSectionsCompleted())
                    .build();

        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.HSE_TI_APPLICATION_SUBMIT);
    }
}
