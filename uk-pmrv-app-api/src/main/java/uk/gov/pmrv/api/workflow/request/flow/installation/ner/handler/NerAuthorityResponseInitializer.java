package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@Service
@RequiredArgsConstructor
public class NerAuthorityResponseInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        final NerProceedToAuthorityDetermination determination =
            (NerProceedToAuthorityDetermination) requestPayload.getDetermination();
        return NerAuthorityResponseRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_AUTHORITY_RESPONSE_PAYLOAD)
            .originalPreliminaryAllocations(determination.getPreliminaryAllocations())
            .build();

    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NER_AUTHORITY_RESPONSE);
    }
}
