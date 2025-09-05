package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper.HSETIMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class HSEApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final HSETIMapper HSETI_MAPPER = Mappers.getMapper(HSETIMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return HSETI_MAPPER.toHSETIApplicationAmendsSubmitRequestTaskPayload((HSETIRequestPayload) request.getPayload());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.HSE_TI_APPLICATION_AMENDS_SUBMIT);
    }
}
