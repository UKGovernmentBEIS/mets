package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRS2ApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final BDRS2RequestPayload requestPayload =
                (BDRS2RequestPayload) request.getPayload();

        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload;

        taskPayload = BDRS2ApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_SUBMIT_PAYLOAD)
                .bdrs2(requestPayload.getBdrs2())
                .bdrs2Attachments(requestPayload.getBdrs2Attachments())
                .bdrs2SectionsCompleted(requestPayload.getBdrs2SectionsCompleted())
                .bdrs2FileVersion(requestPayload.getBdrs2FileVersion())
                .build();

        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDRS2_APPLICATION_SUBMIT);
    }
}
