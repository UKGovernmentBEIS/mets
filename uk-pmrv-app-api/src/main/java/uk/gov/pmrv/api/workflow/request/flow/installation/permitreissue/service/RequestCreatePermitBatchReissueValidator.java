package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByCAValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;

@RequiredArgsConstructor
@Service
public class RequestCreatePermitBatchReissueValidator implements RequestCreateByCAValidator<BatchReissueRequestCreateActionPayload>{
	
	private final RequestQueryService requestQueryService;
	private final PermitBatchReissueQueryService permitBatchReissueQueryService;
	
	@Override
	public void validateAction(CompetentAuthorityEnum competentAuthority,
			BatchReissueRequestCreateActionPayload payload) {
		final boolean inProgressExist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
				RequestType.PERMIT_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority);
		if(inProgressExist) {
			throw new BusinessException(ErrorCode.BATCH_REISSUE_IN_PROGRESS_REQUEST_EXISTS);
		}
		
		final PermitBatchReissueFilters filters = (PermitBatchReissueFilters) payload.getFilters();
		if(!permitBatchReissueQueryService.existAccountsByCAAndFilters(competentAuthority, filters)) {
			throw new BusinessException(ErrorCode.BATCH_REISSUE_ZERO_EMITTERS_SELECTED);
		}
	}

	@Override
	public RequestCreateActionType getType() {
		return RequestCreateActionType.PERMIT_BATCH_REISSUE;
	}

}
