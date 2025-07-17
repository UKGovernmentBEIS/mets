package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueAddCompletedRequestActionService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class EmpReissueDoReissueService {

	private final RequestService requestService;
	private final EmpReissueAccountValidationService empReissueAccountValidationService;
	private final EmissionsMonitoringPlanService emissionsMonitoringPlanService;
	private final EmpReissueUpdatePayloadConsolidationNumberService empReissueUpdatePayloadConsolidationNumberService;
	private final EmpReissueGenerateDocumentsService empReissueGenerateDocumentsService;
	private final ReissueAddCompletedRequestActionService reissueAddCompletedRequestActionService;
	private final ReissueOfficialNoticeService reissueOfficialNoticeService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void doReissue(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final Long accountId = request.getAccountId();

		boolean isAccountApplicable = empReissueAccountValidationService.isAccountApplicableToReissue(request);
		if(!isAccountApplicable) {
			throw new BusinessException(MetsErrorCode.REISSUE_ACCOUNT_NOT_APPLICABLE, accountId);
		}
		
		request.setSubmissionDate(LocalDateTime.now());
		
		emissionsMonitoringPlanService.incrementEmpConsolidationNumber(accountId);
		
		empReissueUpdatePayloadConsolidationNumberService.updateRequestPayloadConsolidationNumber(request);

		empReissueGenerateDocumentsService.generateDocuments(request);

		reissueAddCompletedRequestActionService.add(requestId);
		
		reissueOfficialNoticeService.sendOfficialNotice(request);
	}
	
}
