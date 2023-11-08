package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesWithdrawalApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT_PAYLOAD)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT);
    }
}
