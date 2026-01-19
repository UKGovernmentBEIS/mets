package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceRegistryEventPublisherService {

    private final ApplicationEventPublisher publisher;

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;


    public void publishRegistryEvent(EmpIssuanceUkEtsRequestPayload payload,String requestId,Long accountId) {

        EmissionsMonitoringPlanUkEtsDTO  emissionsMonitoringPlanUkEtsDTO =
                emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        AviationAccountCreatedRegistryEvent aviationAccountCreatedRegistryEvent =
                AviationAccountCreatedRegistryEvent.builder().accountId(accountId).requestId(requestId)
                .emissionsMonitoringPlan(emissionsMonitoringPlanUkEtsDTO.getEmpContainer().getEmissionsMonitoringPlan()).build();

        if(EmpIssuanceDeterminationType.APPROVED.equals(payload.getDetermination().getType())) {
            publisher.publishEvent(aviationAccountCreatedRegistryEvent);


        }
    }

}
