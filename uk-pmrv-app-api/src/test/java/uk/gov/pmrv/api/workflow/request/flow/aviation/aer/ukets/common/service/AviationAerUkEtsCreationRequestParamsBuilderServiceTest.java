package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsCreationRequestParamsBuilderServiceTest {

    @InjectMocks
    private AviationAerUkEtsCreationRequestParamsBuilderService creationRequestParamsBuilderService;

    @Test
    void buildRequestParams() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        RequestParams expectedParams = RequestParams.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .requestPayload(AviationAerUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
                .build())
            .requestMetadata(AviationAerRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_AER)
                .year(year)
                .initiatorRequest(AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_UKETS).build())
                .build())
            .processVars(Map.of(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE,
                DateUtils.convertLocalDateToDate(LocalDate.of(Year.now().getValue(), 3, 31))))
            .build();

        assertEquals(expectedParams, creationRequestParamsBuilderService.buildRequestParams(accountId, year));
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, creationRequestParamsBuilderService.getEmissionTradingScheme());
    }
}