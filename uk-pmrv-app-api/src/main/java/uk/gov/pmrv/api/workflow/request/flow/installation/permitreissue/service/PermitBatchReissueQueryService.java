package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermitBatchReissueQueryService {
	
	private final InstallationAccountQueryService installationAccountQueryService;
	private final PermitQueryService permitQueryService;
	
	public boolean existAccountsByCAAndFilters(CompetentAuthorityEnum ca, PermitBatchReissueFilters filters) {
		return !findAccounts(ca, filters).isEmpty();
	}

	public Map<Long, PermitReissueAccountDetails> findAccountsByCAAndFilters(CompetentAuthorityEnum ca, PermitBatchReissueFilters filters){
		final Map<Long, InstallationAccountIdAndNameAndLegalEntityNameDTO> accountDetails = findAccounts(ca, filters)
				.stream().collect(Collectors.toMap(InstallationAccountIdAndNameAndLegalEntityNameDTO::getAccountId,
						Function.identity()));
		
		final Map<Long, PermitEntityAccountDTO> accountPermitDetails = permitQueryService.getPermitByAccountIds(new ArrayList<>(accountDetails.keySet()));
		
		return accountDetails.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
			final InstallationAccountIdAndNameAndLegalEntityNameDTO accountInfo = accountDetails.get(e.getKey());
			final PermitEntityAccountDTO permitInfo = accountPermitDetails.get(e.getKey());

			return PermitReissueAccountDetails.builder()
					.permitId(permitInfo.getPermitEntityId())
					.installationName(accountInfo.getAccountName())
					.operatorName(accountInfo.getLegalEntityName())
					.build();
		}));
	}

	private Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> findAccounts(CompetentAuthorityEnum ca, PermitBatchReissueFilters filters) {
		if ( (filters.getEmitterTypes().size() == 1 && filters.getEmitterTypes().contains(EmitterType.WASTE)) ||
				!(filters.isFreeAllocation() ^ filters.isNonFreeAllocation())) {
			return installationAccountQueryService
                    .getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(
							ca,
							filters.getAccountStatuses(),
                            filters.getEmitterTypes(),
							filters.getInstallationCategories()); //TODO use count query
		}

		return installationAccountQueryService
                    .getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypesAndFaStatus(
							ca,
							filters.getAccountStatuses(),
                            filters.getEmitterTypes(),
							filters.getInstallationCategories(),
                            filters.isFreeAllocation());
	}
	
}
