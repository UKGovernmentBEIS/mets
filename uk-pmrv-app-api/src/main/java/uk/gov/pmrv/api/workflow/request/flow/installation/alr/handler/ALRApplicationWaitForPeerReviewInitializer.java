package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ALRApplicationWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final ALRRequestPayload requestPayload =
                (ALRRequestPayload) request.getPayload();
        return ALR_MAPPER.toALRApplicationRegulatorReviewSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.ALR_WAIT_FOR_PEER_REVIEW_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_WAIT_FOR_PEER_REVIEW);
    }
}
