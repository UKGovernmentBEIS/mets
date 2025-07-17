package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.mapper.PermanentCessationMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermanentCessationApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final PermanentCessationMapper PERMANENT_CESSATION_MAPPER = Mappers.getMapper(PermanentCessationMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        PermanentCessationRequestPayload requestPayload = (PermanentCessationRequestPayload) request.getPayload();
        return PERMANENT_CESSATION_MAPPER.toPermanentCessationApplicationSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {return Set.of(RequestTaskType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW);}
}
