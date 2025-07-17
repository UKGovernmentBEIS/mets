package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermanentCessationApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final PermanentCessationRequestPayload requestPayload =
                (PermanentCessationRequestPayload) request.getPayload();

        final PermanentCessationApplicationSubmitRequestTaskPayload taskPayload;

        taskPayload = PermanentCessationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMANENT_CESSATION_SUBMIT_PAYLOAD)
                .permanentCessation(requestPayload.getPermanentCessation())
                .permanentCessationAttachments(requestPayload.getPermanentCessationAttachments())
                .permanentCessationSectionsCompleted(requestPayload.getPermanentCessationSectionsCompleted())
                .build();

        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {return Set.of(RequestTaskType.PERMANENT_CESSATION_APPLICATION_SUBMIT);}
}
