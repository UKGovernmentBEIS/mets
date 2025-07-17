package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.mapper.AviationAerCorsia3YearPeriodOffsettingMapper;


@Service
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingPeerReviewInitializer
        implements InitializeRequestTaskHandler {

    private final AviationAerCorsia3YearPeriodOffsettingMapper AER_AVIATION_CORSIA_3YEAR_PERIOD_OFFSETTING_MAPPER =
            Mappers.getMapper(AviationAerCorsia3YearPeriodOffsettingMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                (AviationAerCorsia3YearPeriodOffsettingRequestPayload) request.getPayload();
        return AER_AVIATION_CORSIA_3YEAR_PERIOD_OFFSETTING_MAPPER
                .toAviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload(requestPayload,
                        RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_PEER_REVIEW_PAYLOAD);
    }


    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW);
    }
}
