package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;

public interface AviationAerCreationRequestParamsBuilderService {

    RequestParams buildRequestParams(Long accountId, Year reportingYear);

    EmissionTradingScheme getEmissionTradingScheme();
}
