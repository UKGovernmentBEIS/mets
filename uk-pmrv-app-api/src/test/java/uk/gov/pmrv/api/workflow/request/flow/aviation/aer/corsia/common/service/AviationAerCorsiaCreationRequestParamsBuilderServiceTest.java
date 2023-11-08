package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaCreationRequestParamsBuilderServiceTest {

    @InjectMocks
    private AviationAerCorsiaCreationRequestParamsBuilderService creationRequestParamsBuilderService;

    @Test
    void buildRequestParams() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        RequestParams expectedParams = RequestParams.builder()
            .type(RequestType.AVIATION_AER_CORSIA)
            .accountId(accountId)
            .requestPayload(AviationAerCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                .build())
            .requestMetadata(AviationAerCorsiaRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_AER_CORSIA)
                .year(year)
                .initiatorRequest(AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_CORSIA).build())
                .build())
            .processVars(Map.of(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE,
                DateUtils.convertLocalDateToDate(LocalDate.of(Year.now().getValue(), 4, 30))))
            .build();

        assertEquals(expectedParams, creationRequestParamsBuilderService.buildRequestParams(accountId, year));
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.CORSIA, creationRequestParamsBuilderService.getEmissionTradingScheme());
    }
}