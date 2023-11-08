package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountIdAndNameDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountDetails;

@Service
@RequiredArgsConstructor
public class EmpBatchReissueQueryService {

	private final AviationAccountQueryService aviationAccountQueryService;
	private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	public boolean existAccountsByCAAndFilters(CompetentAuthorityEnum ca, EmpBatchReissueFilters filters) {
		return aviationAccountQueryService.getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
				Set.of(AviationAccountStatus.LIVE),
				filters.getEmissionTradingSchemes(), filters.getReportingStatuses()).size() > 0; // TODO use count query
	}

	public Map<Long, EmpReissueAccountDetails> findAccountsByCAAndFilters(CompetentAuthorityEnum ca, EmpBatchReissueFilters filters){
		final Map<Long, AviationAccountIdAndNameDTO> accountDetails = aviationAccountQueryService.
				getAllByCAAndStatusesAndEmissionTradingSchemesAndReportingStatuses(ca,
						Set.of(AviationAccountStatus.LIVE),
						filters.getEmissionTradingSchemes(), filters.getReportingStatuses())
				.stream().collect(Collectors.toMap(AviationAccountIdAndNameDTO::getAccountId,
						Function.identity()));
		
		final Map<Long, EmpAccountDTO> empAccounts = emissionsMonitoringPlanQueryService.getEmpAccountsByAccountIds(accountDetails.keySet());
		
		return accountDetails.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
			final AviationAccountIdAndNameDTO accountInfo = accountDetails.get(e.getKey());
			final EmpAccountDTO empInfo = empAccounts.get(e.getKey());

			return EmpReissueAccountDetails.builder()
					.empId(empInfo.getEmpId())
					.accountName(accountInfo.getAccountName())
					.build();
		}));
	}
	
}
