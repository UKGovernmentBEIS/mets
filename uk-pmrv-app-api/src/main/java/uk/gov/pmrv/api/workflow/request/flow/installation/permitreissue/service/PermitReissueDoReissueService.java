package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueAddCompletedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PermitReissueDoReissueService {
	
	private final RequestService requestService;
	private final PermitReissueAccountValidationService permitReissueAccountValidationService;
	private final PermitService permitService;
	private final PermitReissueUpdatePayloadConsolidationNumberService reissueUpdatePayloadConsolidationNumberService;
	private final PermitReissueGenerateDocumentsService permitReissueGenerateDocumentsService;
	private final ReissueAddCompletedRequestActionService reissueAddCompletedRequestActionService;
	private final ReissueOfficialNoticeService reissueOfficialNoticeService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void doReissue(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final Long accountId = request.getAccountId();

		boolean isAccountApplicable = permitReissueAccountValidationService.isAccountApplicableToReissue(request);
		if(!isAccountApplicable) {
			throw new BusinessException(MetsErrorCode.REISSUE_ACCOUNT_NOT_APPLICABLE, accountId);
		}
		
		request.setSubmissionDate(LocalDateTime.now());
		
		permitService.incrementPermitConsolidationNumber(accountId);
		
		reissueUpdatePayloadConsolidationNumberService.updateRequestPayloadConsolidationNumber(request);
		
		permitReissueGenerateDocumentsService.generateDocuments(request);
		
		reissueAddCompletedRequestActionService.add(requestId);
		
		reissueOfficialNoticeService.sendOfficialNotice(request);
	}
	
}
