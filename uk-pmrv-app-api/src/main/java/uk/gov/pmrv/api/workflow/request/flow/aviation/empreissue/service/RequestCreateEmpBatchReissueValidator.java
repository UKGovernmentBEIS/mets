package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

@RequiredArgsConstructor
@Service
public class RequestCreateEmpBatchReissueValidator implements RequestCreateByCAValidator<BatchReissueRequestCreateActionPayload>{
	
	private final RequestQueryService requestQueryService;
	private final EmpBatchReissueQueryService empBatchReissueQueryService;
	
	@Override
	public RequestCreateValidationResult validateAction(CompetentAuthorityEnum competentAuthority,
							   BatchReissueRequestCreateActionPayload payload) {
		final boolean inProgressExist = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
				RequestType.EMP_BATCH_REISSUE, RequestStatus.IN_PROGRESS, competentAuthority);
		if(inProgressExist) {
			throw new BusinessException(MetsErrorCode.BATCH_REISSUE_IN_PROGRESS_REQUEST_EXISTS);
		}
		
		final EmpBatchReissueFilters filters = (EmpBatchReissueFilters) payload.getFilters();
		if(!empBatchReissueQueryService.existAccountsByCAAndFilters(competentAuthority, filters)) {
			throw new BusinessException(MetsErrorCode.BATCH_REISSUE_ZERO_EMITTERS_SELECTED);
		}
		return null;
	}

	@Override
	public RequestCreateActionType getType() {
		return RequestCreateActionType.EMP_BATCH_REISSUE;
	}

}
