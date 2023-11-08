package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AviationAccountQueryService aviationAccountQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final AviationAccountInfoDTO aviationAccount = aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId());
        return EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .operatorDetails(EmpOperatorDetails.builder()
                                .operatorName(aviationAccount.getName())
                                .crcoCode(aviationAccount.getCrcoCode())
                                .build())
                        .build())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT);
    }
}
