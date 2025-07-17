package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRApplicationWaitForAmendsInitializer implements InitializeRequestTaskHandler {

    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);
    private final RequestVerificationService requestVerificationService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();

 		requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        return BDR_MAPPER.toBDRApplicationRegulatorReviewSubmitRequestTaskPayload(requestPayload,
            RequestTaskPayloadType.BDR_WAIT_FOR_AMENDS_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDR_WAIT_FOR_AMENDS);
    }
}
