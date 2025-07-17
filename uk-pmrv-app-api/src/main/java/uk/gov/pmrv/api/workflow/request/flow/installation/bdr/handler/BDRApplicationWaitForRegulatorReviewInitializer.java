package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationWaitForRegulatorReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRApplicationWaitForRegulatorReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        BDRRequestMetadata requestMetadata = (BDRRequestMetadata) request.getMetadata();

        return BDRApplicationWaitForRegulatorReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDR_APPLICATION_WAIT_FOR_REGULATOR_REVIEW_PAYLOAD)
                .sendEmailNotification(!requestMetadata.getBdrInitiationType().equals(BDRInitiationType.RE_INITIATED))
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDR_WAIT_FOR_REGULATOR_REVIEW);
    }
}
