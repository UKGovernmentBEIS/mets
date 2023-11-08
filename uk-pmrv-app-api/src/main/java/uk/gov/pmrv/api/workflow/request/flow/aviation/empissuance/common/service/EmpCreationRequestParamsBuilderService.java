package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

public interface EmpCreationRequestParamsBuilderService {

    RequestParams buildRequestParams(Long accountId);

    EmissionTradingScheme getEmissionTradingScheme();
}
