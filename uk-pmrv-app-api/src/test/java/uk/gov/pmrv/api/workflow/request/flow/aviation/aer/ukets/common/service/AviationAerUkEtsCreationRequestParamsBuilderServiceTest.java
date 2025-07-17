package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsCreationRequestParamsBuilderServiceTest {

    @InjectMocks
    private AviationAerUkEtsCreationRequestParamsBuilderService creationRequestParamsBuilderService;
    
    @Mock
    private AviationAerUkEtsBuildEmpOriginatedDataService buildEmpOriginatedDataService;
    
    @Mock
    private AviationAerUkEtsBuildMonitoringPlanVersionsService buildMonitoringPlanVersionsService;

    @Test
    void buildRequestParams() {
        Long accountId = 1L;
        Year reportingYear = Year.of(2023);
        
        EmpUkEtsOriginatedData empOriginatedData = EmpUkEtsOriginatedData.builder()
        		.operatorDetails(AviationOperatorDetails.builder()
        				.operatorName("opename")
        				.build())
        		.build();
        when(buildEmpOriginatedDataService.build(accountId)).thenReturn(empOriginatedData);
        
        List<AviationAerMonitoringPlanVersion> versions = List.of(
        		AviationAerMonitoringPlanVersion.builder()
        		.empId("emp1")
        		.build()
        		);
        when(buildMonitoringPlanVersionsService.build(accountId, reportingYear)).thenReturn(versions);

        RequestParams expectedParams = RequestParams.builder()
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .requestPayload(AviationAerUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
                .empOriginatedData(empOriginatedData)
                .aerMonitoringPlanVersions(versions)
                .build())
            .requestMetadata(AviationAerRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_AER)
                .year(reportingYear)
                .initiatorRequest(AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_UKETS).build())
                .build())
            .processVars(Map.of(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE,
                DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 3, 31))))
            .build();

        assertEquals(expectedParams, creationRequestParamsBuilderService.buildRequestParams(accountId, reportingYear));
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, creationRequestParamsBuilderService.getEmissionTradingScheme());
    }
}