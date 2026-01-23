package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2InitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRS2ApplicationWaitForRegulatorReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        BDRS2RequestMetadata requestMetadata = (BDRS2RequestMetadata) request.getMetadata();


        return BDRS2ApplicationWaitForRegulatorReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.BDRS2_APPLICATION_WAIT_FOR_REGULATOR_REVIEW_PAYLOAD)
                .sendEmailNotification(!requestMetadata.getBdrs2InitiationType().equals(BDRS2InitiationType.RE_INITIATED))
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDRS2_WAIT_FOR_REGULATOR_REVIEW);
    }
}
