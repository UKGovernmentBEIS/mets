package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.InstallationAccountIdAndNameAndLegalEntityNameDTOImpl;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitEntityAccountDTOImpl;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;

@ExtendWith(MockitoExtension.class)
class PermitBatchReissueQueryServiceTest {
	
	@InjectMocks
	private PermitBatchReissueQueryService cut;

	@Mock
	private InstallationAccountQueryService installationAccountQueryService;
	
	@Mock
	private PermitQueryService permitQueryService;
	
	@Test
	void existAccountsByCAAndFilters() {
		CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
		Set<InstallationAccountStatus> statuses = Set.of(InstallationAccountStatus.LIVE);
		Set<InstallationCategory> categories = Set.of(InstallationCategory.A);
		Set<EmitterType> emitterTypes = Set.of(EmitterType.GHGE);
		
		PermitBatchReissueFilters filters = PermitBatchReissueFilters.builder()
				.accountStatuses(statuses)
				.installationCategories(categories)
				.emitterTypes(emitterTypes)
				.build();
		
		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> accounts = Set.of(
				InstallationAccountIdAndNameAndLegalEntityNameDTOImpl.builder().accountId(1L).accountName("acc1").legalEntityName("le1").build(),
				InstallationAccountIdAndNameAndLegalEntityNameDTOImpl.builder().accountId(2L).accountName("acc2").legalEntityName("le2").build()
				);
		
		when(installationAccountQueryService.getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories))
		.thenReturn(accounts);
		
		boolean result = cut.existAccountsByCAAndFilters(ca, filters);
		
		assertThat(result).isTrue();
		
		verify(installationAccountQueryService, times(1)).getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories);
		
	}
	
	@Test
	void findAccountsByCAAndFilters() {
		CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
		Set<InstallationAccountStatus> statuses = Set.of(InstallationAccountStatus.LIVE);
		Set<InstallationCategory> categories = Set.of(InstallationCategory.A);
		Set<EmitterType> emitterTypes = Set.of(EmitterType.GHGE);
		
		PermitBatchReissueFilters filters = PermitBatchReissueFilters.builder()
				.accountStatuses(statuses)
				.installationCategories(categories)
				.emitterTypes(emitterTypes)
				.build();
		
		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> accounts = Set.of(
				InstallationAccountIdAndNameAndLegalEntityNameDTOImpl.builder().accountId(1L).accountName("acc1").legalEntityName("le1").build(),
				InstallationAccountIdAndNameAndLegalEntityNameDTOImpl.builder().accountId(2L).accountName("acc2").legalEntityName("le2").build()
				);
		
		when(installationAccountQueryService.getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories))
				.thenReturn(accounts);
		
		Map<Long, PermitEntityAccountDTO> accountPermitDetails = Map.of(
				1L, PermitEntityAccountDTOImpl.builder().accountId(1L).permitEntityId("permitId1").build(),
				2L, PermitEntityAccountDTOImpl.builder().accountId(2L).permitEntityId("permitId2").build()
				);
		
		when(permitQueryService.getPermitByAccountIds(List.of(1L, 2L))).thenReturn(accountPermitDetails);
		
		Map<Long, PermitReissueAccountDetails> result = cut.findAccountsByCAAndFilters(ca, filters);
		
		assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
				1L, PermitReissueAccountDetails.builder().installationName("acc1").operatorName("le1").permitId("permitId1").build(),
				2L, PermitReissueAccountDetails.builder().installationName("acc2").operatorName("le2").permitId("permitId2").build()
				));
		verify(installationAccountQueryService, times(1)).getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories);
		verify(permitQueryService, times(1)).getPermitByAccountIds(List.of(1L, 2L));
	}
	
}
