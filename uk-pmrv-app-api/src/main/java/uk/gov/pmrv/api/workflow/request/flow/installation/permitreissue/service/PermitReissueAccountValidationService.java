package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;

@Service
@RequiredArgsConstructor
class PermitReissueAccountValidationService {
	
	private final ReissueQueryService reissueQueryService;
	private final InstallationAccountQueryService accountQueryService;

	public boolean isAccountApplicableToReissue(Request request) {
		final InstallationAccountDTO accountDTO = accountQueryService.getAccountDTOById(request.getAccountId());
		final Request batchReissue = reissueQueryService.getBatchRequest(request);
		final BatchReissueRequestPayload batchReissuePayload = (BatchReissueRequestPayload) batchReissue.getPayload();
		final PermitBatchReissueFilters filters = (PermitBatchReissueFilters) batchReissuePayload.getFilters();
		
		return (ObjectUtils.isEmpty(filters.getInstallationCategories()) || filters.getInstallationCategories().contains(accountDTO.getInstallationCategory())) &&
				(filters.getAccountStatuses().isEmpty() || filters.getAccountStatuses().contains(accountDTO.getStatus())) &&
				(filters.getEmitterTypes().isEmpty() || filters.getEmitterTypes().contains(accountDTO.getEmitterType()));
	}
}
