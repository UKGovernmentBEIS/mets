package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AviationAccountQueryService aviationAccountQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final AviationAccountInfoDTO aviationAccount = aviationAccountQueryService.getAviationAccountInfoDTOById(request.getAccountId());
        return EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .operatorDetails(EmpCorsiaOperatorDetails.builder()
                        .operatorName(aviationAccount.getName())
                        .build())
                    .build())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT);
    }
}
