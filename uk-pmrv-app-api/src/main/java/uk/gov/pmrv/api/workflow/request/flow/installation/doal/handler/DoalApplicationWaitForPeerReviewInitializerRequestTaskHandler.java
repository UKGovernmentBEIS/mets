package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper.DoalMapper;

import java.util.Set;

@Service
public class DoalApplicationWaitForPeerReviewInitializerRequestTaskHandler implements InitializeRequestTaskHandler {

    private static final DoalMapper DOAL_MAPPER = Mappers.getMapper(DoalMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        return DOAL_MAPPER.toDoalApplicationSubmitRequestTaskPayload(
                requestPayload, RequestTaskPayloadType.DOAL_WAIT_FOR_PEER_REVIEW_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.DOAL_WAIT_FOR_PEER_REVIEW);
    }
}
