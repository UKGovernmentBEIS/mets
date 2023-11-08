package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NerMapper;

@Service
@RequiredArgsConstructor
public class NerWaitForAmendsInitializer implements InitializeRequestTaskHandler {

    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        return NER_MAPPER.toNerReviewRequestTaskPayload(
            requestPayload,
            RequestTaskPayloadType.NER_WAIT_FOR_AMENDS_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NER_WAIT_FOR_AMENDS);
    }
}
