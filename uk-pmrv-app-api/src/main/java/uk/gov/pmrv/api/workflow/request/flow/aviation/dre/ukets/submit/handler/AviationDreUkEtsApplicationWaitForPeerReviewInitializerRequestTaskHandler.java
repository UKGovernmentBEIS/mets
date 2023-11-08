package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.mapper.AviationDreUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class AviationDreUkEtsApplicationWaitForPeerReviewInitializerRequestTaskHandler implements InitializeRequestTaskHandler {

    private static final AviationDreUkEtsMapper DRE_MAPPER = Mappers.getMapper(AviationDreUkEtsMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
        final AviationDreUkEtsApplicationSubmitRequestTaskPayload taskPayload = DRE_MAPPER
            .toAviationDreUkEtsApplicationSubmitRequestTaskPayload(
            requestPayload, RequestTaskPayloadType.AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW);
    }

}
