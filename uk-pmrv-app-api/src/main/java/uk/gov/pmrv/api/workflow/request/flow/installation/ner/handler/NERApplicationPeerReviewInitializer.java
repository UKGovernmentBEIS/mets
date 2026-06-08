package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class NERApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final NERMapper NER_MAPPER = Mappers.getMapper(NERMapper.class);
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final NerRequestPayload requestPayload =
                (NerRequestPayload) request.getPayload();
        return NER_MAPPER.toNERApplicationRegulatorReviewSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.NER_APPLICATION_PEER_REVIEW_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NER_APPLICATION_PEER_REVIEW);
    }
}
