package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

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

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;

@ExtendWith(MockitoExtension.class)
class PermitReissueAccountValidationServiceTest {

	@InjectMocks
	private PermitReissueAccountValidationService cut;

	@Mock
	private ReissueQueryService reissueQueryService;
	
	@Mock
	private InstallationAccountQueryService accountQueryService;
	
	@Test
	void isAccountApplicableToReissue_result_true() {
		Long accountId = 1L;
		Request request = Request.builder()
				.accountId(accountId)
				.build();
		
		InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
				.installationCategory(InstallationCategory.A)
				.status(InstallationAccountStatus.LIVE)
				.emitterType(EmitterType.GHGE)
				.build();
		
		Request batchReissue = Request.builder()
				.payload(BatchReissueRequestPayload.builder()
						.filters(PermitBatchReissueFilters.builder()
								.installationCategories(Set.of(InstallationCategory.A))
								.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
								.emitterTypes(Set.of(EmitterType.GHGE))
								.build())
						.build())
				.build();
		
		when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);
		when(reissueQueryService.getBatchRequest(request)).thenReturn(batchReissue);
		
		boolean result = cut.isAccountApplicableToReissue(request);
		
		assertThat(result).isTrue();
		verify(accountQueryService, times(1)).getAccountDTOById(accountId);
		verify(reissueQueryService, times(1)).getBatchRequest(request);
	}
	
	@Test
	void isAccountApplicableToReissue_result_false() {
		Long accountId = 1L;
		Request request = Request.builder()
				.accountId(accountId)
				.build();
		
		InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
				.installationCategory(InstallationCategory.A_LOW_EMITTER)
				.status(InstallationAccountStatus.LIVE)
				.emitterType(EmitterType.GHGE)
				.build();
		
		Request batchReissue = Request.builder()
				.payload(BatchReissueRequestPayload.builder()
						.filters(PermitBatchReissueFilters.builder()
								.installationCategories(Set.of(InstallationCategory.A))
								.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
								.emitterTypes(Set.of(EmitterType.GHGE))
								.build())
						.build())
				.build();
		
		when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);
		when(reissueQueryService.getBatchRequest(request)).thenReturn(batchReissue);
		
		boolean result = cut.isAccountApplicableToReissue(request);
		
		assertThat(result).isFalse();
		verify(accountQueryService, times(1)).getAccountDTOById(accountId);
		verify(reissueQueryService, times(1)).getBatchRequest(request);
	}
	
	@Test
	void isAccountApplicableToReissue_if_empty_result_is_true() {
		Long accountId = 1L;
		Request request = Request.builder()
				.accountId(accountId)
				.build();
		
		InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
				.installationCategory(InstallationCategory.A)
				.status(InstallationAccountStatus.LIVE)
				.emitterType(EmitterType.GHGE)
				.build();
		
		Request batchReissue = Request.builder()
				.payload(BatchReissueRequestPayload.builder()
						.filters(PermitBatchReissueFilters.builder()
								.installationCategories(Set.of())
								.accountStatuses(Set.of())
								.emitterTypes(Set.of())
								.build())
						.build())
				.build();
		
		when(accountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);
		when(reissueQueryService.getBatchRequest(request)).thenReturn(batchReissue);
		
		boolean result = cut.isAccountApplicableToReissue(request);
		
		assertThat(result).isTrue();
		verify(accountQueryService, times(1)).getAccountDTOById(accountId);
		verify(reissueQueryService, times(1)).getBatchRequest(request);
	}
}
