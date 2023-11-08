package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.mapper.EmpVariationUkEtsAmendSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpVariationUkEtsAmendSubmitMapper MAPPER = Mappers.getMapper(EmpVariationUkEtsAmendSubmitMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		
		RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
        return MAPPER.toEmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload(
            (EmpVariationUkEtsRequestPayload)request.getPayload(),
            aviationAccountInfo,
            RequestTaskPayloadType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD
        );
	}

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT);
    }
}
