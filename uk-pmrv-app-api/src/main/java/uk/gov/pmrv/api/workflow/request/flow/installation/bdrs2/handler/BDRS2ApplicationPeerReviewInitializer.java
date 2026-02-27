package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRS2ApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final BDRS2Mapper BDRS2_MAPPER = Mappers.getMapper(BDRS2Mapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final BDRS2RequestPayload requestPayload =
                (BDRS2RequestPayload) request.getPayload();
        return BDRS2_MAPPER.toBDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload(requestPayload, RequestTaskPayloadType.BDRS2_APPLICATION_PEER_REVIEW_PAYLOAD);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDRS2_APPLICATION_PEER_REVIEW);
    }
}
