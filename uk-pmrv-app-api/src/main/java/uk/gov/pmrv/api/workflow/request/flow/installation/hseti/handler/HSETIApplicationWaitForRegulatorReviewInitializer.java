package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationWaitForRegulatorReviewRequestTaskPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HSETIApplicationWaitForRegulatorReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return HSETIApplicationWaitForRegulatorReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.HSE_TI_APPLICATION_WAIT_FOR_REGULATOR_REVIEW_PAYLOAD)
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.HSE_TI_WAIT_FOR_REGULATOR_REVIEW);
    }
}
