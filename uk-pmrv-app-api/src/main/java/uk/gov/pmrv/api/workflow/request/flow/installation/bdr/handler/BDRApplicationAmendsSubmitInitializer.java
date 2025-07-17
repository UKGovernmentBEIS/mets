package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;

import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return BDR_MAPPER.toBDRApplicationAmendsSubmitRequestTaskPayload((BDRRequestPayload) request.getPayload(), (BDRRequestMetadata) request.getMetadata());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.BDR_APPLICATION_AMENDS_SUBMIT);
    }
}
