package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.AviationAccountIdAndNameDTOImpl;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpAccountDTOImpl;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountDetails;

@ExtendWith(MockitoExtension.class)
class EmpBatchReissueQueryServiceTest {

	@InjectMocks
	private EmpBatchReissueQueryService cut;

	@Mock
	private AviationAccountQueryService aviationAccountQueryService;
	
	@Mock
	private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Test
	void existAccountsByCAAndFilters() {
		CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
		Set<EmissionTradingScheme> emissionTradingSchemes = Set.of(EmissionTradingScheme.UK_ETS_AVIATION);
		Set<AviationAccountReportingStatus> reportingStatuses = Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT);
		
		EmpBatchReissueFilters filters = EmpBatchReissueFilters.builder()
				.emissionTradingSchemes(emissionTradingSchemes)
				.reportingStatuses(reportingStatuses)
				.build();
		
		Set<AviationAccountIdAndNameDTO> accounts = Set.of(
				AviationAccountIdAndNameDTOImpl.builder().accountId(1L).accountName("acc1").build(),
				AviationAccountIdAndNameDTOImpl.builder().accountId(2L).accountName("acc2").build()
				);
		
		when(aviationAccountQueryService.getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
				Set.of(AviationAccountStatus.LIVE), emissionTradingSchemes, reportingStatuses))
				.thenReturn(accounts);
		
		boolean result = cut.existAccountsByCAAndFilters(ca, filters);
		
		assertThat(result).isTrue();
		
		verify(aviationAccountQueryService, times(1))
				.getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
						Set.of(AviationAccountStatus.LIVE), emissionTradingSchemes, reportingStatuses);
		
	}
	
	@Test
	void findAccountsByCAAndFilters() {
		CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
		Set<EmissionTradingScheme> emissionTradingSchemes = Set.of(EmissionTradingScheme.UK_ETS_AVIATION);
		Set<AviationAccountReportingStatus> reportingStatuses = Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT);
		
		EmpBatchReissueFilters filters = EmpBatchReissueFilters.builder()
				.emissionTradingSchemes(emissionTradingSchemes)
				.reportingStatuses(reportingStatuses)
				.build();
		
		Set<AviationAccountIdAndNameDTO> accounts = Set.of(
				AviationAccountIdAndNameDTOImpl.builder().accountId(1L).accountName("acc1").build(),
				AviationAccountIdAndNameDTOImpl.builder().accountId(2L).accountName("acc2").build()
				);
		
		when(aviationAccountQueryService.getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
				Set.of(AviationAccountStatus.LIVE), emissionTradingSchemes, reportingStatuses))
				.thenReturn(accounts);
		
		Map<Long, EmpAccountDTO> accountEmpDetails = Map.of(
				1L, EmpAccountDTOImpl.builder().accountId(1L).empId("empId1").build(),
				2L, EmpAccountDTOImpl.builder().accountId(2L).empId("empId2").build()
				);
		
		when(emissionsMonitoringPlanQueryService.getEmpAccountsByAccountIds(Set.of(1L, 2L))).thenReturn(accountEmpDetails);
		
		Map<Long, EmpReissueAccountDetails> result = cut.findAccountsByCAAndFilters(ca, filters);
		
		assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
				1L, EmpReissueAccountDetails.builder().accountName("acc1").empId("empId1").build(),
				2L, EmpReissueAccountDetails.builder().accountName("acc2").empId("empId2").build()
				));
				verify(aviationAccountQueryService, times(1))
						.getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
								Set.of(AviationAccountStatus.LIVE), emissionTradingSchemes, reportingStatuses);
		verify(emissionsMonitoringPlanQueryService, times(1)).getEmpAccountsByAccountIds(Set.of(1L, 2L));
	}
}
