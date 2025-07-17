package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.mapper.AerAviationCorsiaAnnualOffsettingMapper;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingApplicationWaitForPeerReviewInitializer
        implements InitializeRequestTaskHandler {

    private static final AerAviationCorsiaAnnualOffsettingMapper AER_AVIATION_CORSIA_ANNUAL_OFFSETTING_MAPPER = Mappers.getMapper(AerAviationCorsiaAnnualOffsettingMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();
        return AER_AVIATION_CORSIA_ANNUAL_OFFSETTING_MAPPER.toAviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload(requestPayload,
                this.getRequestTaskPayloadType());
    }


    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW);
    }

    private RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW_PAYLOAD;
    }
}
