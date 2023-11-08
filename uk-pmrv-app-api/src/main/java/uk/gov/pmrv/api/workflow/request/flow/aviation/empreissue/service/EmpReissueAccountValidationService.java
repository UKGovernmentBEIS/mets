package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueQueryService;

@Service
@RequiredArgsConstructor
class EmpReissueAccountValidationService {

	private final AviationAccountQueryService accountQueryService;
	private final ReissueQueryService reissueQueryService;

	public boolean isAccountApplicableToReissue(Request request) {
		final AviationAccountDTO account = accountQueryService.getAviationAccountDTOById(request.getAccountId());
		final Request batchReissue = reissueQueryService.getBatchRequest(request);
		final BatchReissueRequestPayload batchReissuePayload = (BatchReissueRequestPayload) batchReissue.getPayload();
		final EmpBatchReissueFilters filters = (EmpBatchReissueFilters) batchReissuePayload.getFilters();
		
		return (ObjectUtils.isEmpty(filters.getEmissionTradingSchemes()) || filters.getEmissionTradingSchemes().contains(account.getEmissionTradingScheme())) &&
				(filters.getReportingStatuses().isEmpty() || filters.getReportingStatuses().contains(account.getReportingStatus()));
	}
	
}
