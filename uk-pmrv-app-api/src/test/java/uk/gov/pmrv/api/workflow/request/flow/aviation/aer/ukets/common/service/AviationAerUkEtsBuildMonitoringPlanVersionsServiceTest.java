package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerMonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service.EmpVariationUkEtsRequestQueryService;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsBuildMonitoringPlanVersionsServiceTest {

	@InjectMocks
    private AviationAerUkEtsBuildMonitoringPlanVersionsService cut;
    
    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    
    @Mock
    private EmpVariationUkEtsRequestQueryService empVariationRequestQueryService;
    
    @Mock
    private AviationAerRequestQueryService aerRequestQueryService;
    
    @Test
    void build_emp_exists() {
    	Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = "empId";
        
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
        
        when(empVariationRequestQueryService.findApprovedVariationRequests(accountId)).thenReturn(
                List.of(
                		createEmpVariationRequestInfo("requestId1", LocalDateTime.of(reportingYear.getValue(), 5, 17, 11, 15), 10),
                        createEmpVariationRequestInfo("requestId2", LocalDateTime.of(reportingYear.getValue(), 4, 17, 11, 15), 9),
                        createEmpVariationRequestInfo("requestId3", LocalDateTime.of(reportingYear.getValue(), 2, 15, 12, 20), 8),
                        createEmpVariationRequestInfo("requestId4", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 7, 15, 12, 20), 7),
                        createEmpVariationRequestInfo("requestId5", LocalDateTime.of(reportingYear.minusYears(1).getValue(), 4, 16, 8, 25), 6)
                ));
        
		when(aerRequestQueryService.findEndDateOfApprovedEmpIssuanceByAccountId(accountId,
				RequestType.EMP_ISSUANCE_UKETS))
				.thenReturn(Optional.of(LocalDateTime.of(reportingYear.minusYears(2).getValue(), 2, 15, 12, 20)));
		
		List<AviationAerMonitoringPlanVersion> expectedResult = List.of(
				createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.minusYears(1).getValue(), 7, 15), 7),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 2, 15), 8),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 4, 17), 9),
                createAerMonitoringPlanVersion(empId, LocalDate.of(reportingYear.getValue(), 5, 17), 10)
                );
		
		List<AviationAerMonitoringPlanVersion> actualResult = cut.build(accountId, reportingYear);
		
		assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult);
		
		verify(emissionsMonitoringPlanQueryService, times(1)).getEmpIdByAccountId(accountId);
		verify(empVariationRequestQueryService, times(1)).findApprovedVariationRequests(accountId);
		verify(aerRequestQueryService, times(1)).findEndDateOfApprovedEmpIssuanceByAccountId(accountId, RequestType.EMP_ISSUANCE_UKETS);
    }
    
    @Test
    void build_emp_not_exists() {
    	Year reportingYear = Year.now().minusYears(1);
        Long accountId = 1L;
        String empId = null;
        
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.ofNullable(empId));
        
		List<AviationAerMonitoringPlanVersion> actualResult = cut.build(accountId, reportingYear);
		
		assertThat(actualResult).isEmpty();
		
		verify(emissionsMonitoringPlanQueryService, times(1)).getEmpIdByAccountId(accountId);
		verifyNoInteractions(empVariationRequestQueryService);
        verifyNoInteractions(aerRequestQueryService);
    }
    
    private AviationAerMonitoringPlanVersion createAerMonitoringPlanVersion(String empId, LocalDate approvalDate, Integer consolidationNumber) {
        return AviationAerMonitoringPlanVersion.builder()
            .empId(empId)
            .empApprovalDate(approvalDate)
            .empConsolidationNumber(consolidationNumber)
            .build();
    }
    
    private EmpVariationRequestInfo createEmpVariationRequestInfo(String requestId, LocalDateTime endDate, Integer consolidationNumber) {
        return EmpVariationRequestInfo.builder()
                .id(requestId)
                .endDate(endDate)
                .metadata(EmpVariationRequestMetadata.builder().type(RequestMetadataType.EMP_VARIATION).empConsolidationNumber(consolidationNumber).build())
                .build();
    }

}
