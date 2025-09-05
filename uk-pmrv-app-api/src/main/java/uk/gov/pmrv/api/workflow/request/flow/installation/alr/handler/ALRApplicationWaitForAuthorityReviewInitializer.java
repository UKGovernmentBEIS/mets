package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationWaitForAuthorityReviewRequestTaskPayload;


import java.util.Set;

@Service
@RequiredArgsConstructor
public class ALRApplicationWaitForAuthorityReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return ALRApplicationWaitForAuthorityReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.ALR_WAIT_FOR_AUTHORITY_REVIEW_PAYLOAD)
                .sendEmailNotification(false)
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_WAIT_FOR_AUTHORITY_REVIEW);
    }
}
