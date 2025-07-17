package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.mapper.EmpVariationCorsiaSubmitRegulatorLedMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskInitializer
    implements InitializeRequestTaskHandler {

    private final EmissionsMonitoringPlanQueryService empQueryService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpVariationCorsiaMapper VARIATION_CORSIA_MAPPER =
        Mappers.getMapper(EmpVariationCorsiaMapper.class);
    private static final EmpVariationCorsiaSubmitRegulatorLedMapper REGULATOR_LED_MAPPER =
        Mappers.getMapper(EmpVariationCorsiaSubmitRegulatorLedMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();

        final EmissionsMonitoringPlanCorsiaContainer originalEmpContainer =
            empQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(request.getAccountId())
                .map(EmissionsMonitoringPlanCorsiaDTO::getEmpContainer)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        final RequestAviationAccountInfo accountInfo =
            requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload;
        final boolean alreadyDetermined = requestPayload.getReasonRegulatorLed() != null;
        if (alreadyDetermined) {
            requestTaskPayload = REGULATOR_LED_MAPPER
                .toEmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload(requestPayload, accountInfo,
                    RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
        } else {
            final EmissionsMonitoringPlanCorsia emp = VARIATION_CORSIA_MAPPER.cloneEmissionsMonitoringPlanCorsia(
                originalEmpContainer.getEmissionsMonitoringPlan(),
                accountInfo.getOperatorName()
            );

            requestTaskPayload = EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD)
                .originalEmpContainer(originalEmpContainer)
                .emissionsMonitoringPlan(emp)
                .serviceContactDetails(accountInfo.getServiceContactDetails())
                .empAttachments(originalEmpContainer.getEmpAttachments())
                .build();
        }

        return requestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT);
    }
}