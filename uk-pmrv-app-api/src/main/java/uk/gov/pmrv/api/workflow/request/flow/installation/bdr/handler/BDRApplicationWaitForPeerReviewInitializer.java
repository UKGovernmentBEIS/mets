package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRApplicationWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final BDRRequestPayload requestPayload =
                (BDRRequestPayload) request.getPayload();
        return BDR_MAPPER.toBDRApplicationRegulatorReviewSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.BDR_WAIT_FOR_PEER_REVIEW_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDR_WAIT_FOR_PEER_REVIEW);
    }
}
