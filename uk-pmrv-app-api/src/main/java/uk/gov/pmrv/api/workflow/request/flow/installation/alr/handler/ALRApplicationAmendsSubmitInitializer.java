package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

import java.util.Set;

@Service
public class ALRApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return ALR_MAPPER.toALRApplicationAmendsSubmitRequestTaskPayload((ALRRequestPayload) request.getPayload(), (ALRRequestMetaData) request.getMetadata());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_APPLICATION_AMENDS_SUBMIT);
    }
}
