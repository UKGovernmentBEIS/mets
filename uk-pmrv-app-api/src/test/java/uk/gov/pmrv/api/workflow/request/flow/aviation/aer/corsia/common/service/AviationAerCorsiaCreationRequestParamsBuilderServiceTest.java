package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
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
    
    @Mock
    private AviationAerCorsiaBuildEmpOriginatedDataService buildEmpOriginatedDataService;
    
    @Mock
    private AviationAerCorsiaBuildMonitoringPlanVersionsService buildMonitoringPlanVersionsService;

    @Test
    void buildRequestParams() {
        Long accountId = 1L;
        Year reportingYear = Year.of(2023);

        EmpCorsiaOriginatedData empOriginatedData = EmpCorsiaOriginatedData.builder()
        		.operatorDetails(AviationCorsiaOperatorDetails.builder()
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
                .type(RequestType.AVIATION_AER_CORSIA)
                .accountId(accountId)
                .requestPayload(AviationAerCorsiaRequestPayload.builder()
                    .payloadType(RequestPayloadType.AVIATION_AER_CORSIA_REQUEST_PAYLOAD)
                    .empOriginatedData(empOriginatedData)
                    .aerMonitoringPlanVersions(versions)
                    .build())
                .requestMetadata(AviationAerCorsiaRequestMetadata.builder()
                    .type(RequestMetadataType.AVIATION_AER_CORSIA)
                    .year(reportingYear)
                    .initiatorRequest(AerInitiatorRequest.builder().type(RequestType.AVIATION_AER_CORSIA).build())
                    .build())
                .processVars(Map.of(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE,
                    DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 4, 30))))
                .build();
        
        assertEquals(expectedParams, creationRequestParamsBuilderService.buildRequestParams(accountId, reportingYear));
        
        verify(buildEmpOriginatedDataService, times(1)).build(accountId);
        verify(buildMonitoringPlanVersionsService, times(1)).build(accountId, reportingYear);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.CORSIA, creationRequestParamsBuilderService.getEmissionTradingScheme());
    }
}