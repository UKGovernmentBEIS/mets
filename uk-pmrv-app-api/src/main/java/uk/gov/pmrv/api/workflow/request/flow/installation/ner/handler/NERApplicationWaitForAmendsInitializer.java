package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class NERApplicationWaitForAmendsInitializer implements InitializeRequestTaskHandler {

    private static final NERMapper NER_MAPPER = Mappers.getMapper(NERMapper.class);
    private final RequestVerificationService requestVerificationService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
                request.getVerificationBodyId());

        return NER_MAPPER.toNERApplicationRegulatorReviewSubmitRequestTaskPayload(requestPayload,
                RequestTaskPayloadType.NER_WAIT_FOR_AMENDS_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NER_WAIT_FOR_AMENDS);
    }
}
