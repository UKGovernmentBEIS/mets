package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@Service
public class AviationAerCorsiaCreationRequestParamsBuilderService implements
    AviationAerCreationRequestParamsBuilderService {

    @Override
    public RequestParams buildRequestParams(Long accountId, Year reportingYear) {
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE,
            DateUtils.convertLocalDateToDate(LocalDate.of(Year.now().getValue(), 4, 30)));

        return RequestParams.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
            .accountId(accountId)
            .requestPayload(AviationAerCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                .build())
            .requestMetadata(AviationAerCorsiaRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_AER_CORSIA)
                .year(reportingYear)
                .initiatorRequest(AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_CORSIA).build())
                .build())
            .processVars(processVars)
            .build();
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }
}
