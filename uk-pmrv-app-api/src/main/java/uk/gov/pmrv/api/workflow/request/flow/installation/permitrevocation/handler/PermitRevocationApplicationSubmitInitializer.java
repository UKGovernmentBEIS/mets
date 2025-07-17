package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.mapper.PermitRevocationMapper;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitRevocationApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private static final PermitRevocationMapper PERMIT_REVOCATION_MAPPER = Mappers.getMapper(PermitRevocationMapper.class);
    
    private final PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
    	paymentDetermineAmountServiceFacade.resolveAmountAndPopulateRequestPayload(request.getId());
		return PERMIT_REVOCATION_MAPPER
				.toApplicationSubmitRequestTaskPayload((PermitRevocationRequestPayload) request.getPayload());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT);
    }
}
