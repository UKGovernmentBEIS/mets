package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueQueryService;

@ExtendWith(MockitoExtension.class)
class EmpReissueAccountValidationServiceTest {

	@InjectMocks
	private EmpReissueAccountValidationService cut;

	@Mock
	private ReissueQueryService reissueQueryService;
	
	@Mock
	private AviationAccountQueryService aviationAccountQueryService;
	
	@Test
	void isAccountApplicableToReissue_result_true() {
		Long accountId = 1L;
		Request request = Request.builder()
				.accountId(accountId)
				.build();
		
		AviationAccountDTO accountDTO = AviationAccountDTO.builder()
				.emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
				.reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
				.build();
		
		Request batchReissue = Request.builder()
				.payload(BatchReissueRequestPayload.builder()
						.filters(EmpBatchReissueFilters.builder()
								.emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
								.reportingStatuses(Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT))
								.build())
						.build())
				.build();
		
		when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);
		when(reissueQueryService.getBatchRequest(request)).thenReturn(batchReissue);
		
		boolean result = cut.isAccountApplicableToReissue(request);
		
		assertThat(result).isTrue();
		verify(aviationAccountQueryService, times(1)).getAviationAccountDTOById(accountId);
		verify(reissueQueryService, times(1)).getBatchRequest(request);
	}
	
	@Test
	void isAccountApplicableToReissue_result_false() {
		Long accountId = 1L;
		Request request = Request.builder()
				.accountId(accountId)
				.build();
		
		AviationAccountDTO accountDTO = AviationAccountDTO.builder()
				.emissionTradingScheme(EmissionTradingScheme.CORSIA)
				.reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
				.build();
		
		Request batchReissue = Request.builder()
				.payload(BatchReissueRequestPayload.builder()
						.filters(EmpBatchReissueFilters.builder()
								.emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_AVIATION))
								.reportingStatuses(Set.of(AviationAccountReportingStatus.REQUIRED_TO_REPORT))
								.build())
						.build())
				.build();
		
		when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);
		when(reissueQueryService.getBatchRequest(request)).thenReturn(batchReissue);
		
		boolean result = cut.isAccountApplicableToReissue(request);
		
		assertThat(result).isFalse();
		verify(aviationAccountQueryService, times(1)).getAviationAccountDTOById(accountId);
		verify(reissueQueryService, times(1)).getBatchRequest(request);
	}
	
}
