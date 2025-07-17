package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmissionMonitoringPlanCreationService {

    private final List<EmpCreationRequestParamsBuilderService> empCreationRequestParamsBuilderServices;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequestEmissionMonitoringPlan(Long accountId, EmissionTradingScheme emissionTradingScheme) {
        RequestParams requestParams = getRequestParamsBuilderService(emissionTradingScheme)
            .map(service -> service.buildRequestParams(accountId))
            .orElseThrow(() -> new BusinessException(MetsErrorCode.NO_EMP_SERVICE_FOUND));

            startProcessRequestService.startProcess(requestParams);
    }

    private Optional<EmpCreationRequestParamsBuilderService> getRequestParamsBuilderService(EmissionTradingScheme scheme) {
        return empCreationRequestParamsBuilderServices.stream()
            .filter(service -> service.getEmissionTradingScheme().equals(scheme))
            .findFirst();
    }
}
