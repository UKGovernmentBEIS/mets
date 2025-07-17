package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsCreationRequestParamsBuilderService implements
    AviationAerCreationRequestParamsBuilderService {

	private final AviationAerUkEtsBuildEmpOriginatedDataService buildEmpOriginatedDataService;
	private final AviationAerUkEtsBuildMonitoringPlanVersionsService buildMonitoringPlanVersionsService;
	
    @Override
    public RequestParams buildRequestParams(Long accountId, Year reportingYear) {
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE,
            DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 3, 31)));

        return RequestParams.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .requestPayload(AviationAerUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
                .empOriginatedData(buildEmpOriginatedDataService.build(accountId))
                .aerMonitoringPlanVersions(buildMonitoringPlanVersionsService.build(accountId, reportingYear))
                .build())
            .requestMetadata(AviationAerRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_AER)
                .year(reportingYear)
                .initiatorRequest(AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_UKETS).build())
                .build())
            .processVars(processVars)
            .build();
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.UK_ETS_AVIATION;
    }
}
