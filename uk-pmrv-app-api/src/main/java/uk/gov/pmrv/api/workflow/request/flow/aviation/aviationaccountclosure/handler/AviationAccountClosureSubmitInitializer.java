package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.handler;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class AviationAccountClosureSubmitInitializer implements InitializeRequestTaskHandler {

	@Override
    public RequestTaskPayload initializePayload(Request request) {

        return AviationAccountClosureSubmitRequestTaskPayload
        		.builder()
        		.payloadType(RequestTaskPayloadType.AVIATION_ACCOUNT_CLOSURE_SUBMIT_PAYLOAD)
        		.build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_ACCOUNT_CLOSURE_SUBMIT);
    }
}
