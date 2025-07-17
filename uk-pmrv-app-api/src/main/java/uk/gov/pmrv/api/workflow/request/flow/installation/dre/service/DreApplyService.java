package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSaveApplicationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class DreApplyService {
	
	private final DreValidatorService dreValidatorService;
	private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
	
	@Transactional
    public void applySaveAction(
            DreSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        DreApplicationSubmitRequestTaskPayload taskPayload = (DreApplicationSubmitRequestTaskPayload)requestTask.getPayload();
        taskPayload.setDre(taskActionPayload.getDre());
        taskPayload.setSectionCompleted(taskActionPayload.isSectionCompleted());
    }
	
	@Transactional
	public void applySubmitNotify(RequestTask requestTask, DecisionNotification decisionNotification, AppUser appUser) {
		final DreApplicationSubmitRequestTaskPayload requestTaskPayload = (DreApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		final Dre dre = requestTaskPayload.getDre();
		
		dreValidatorService.validateDre(dre);
		if(!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
			 throw new BusinessException(ErrorCode.FORM_VALIDATION);
		}

		final DreRequestPayload requestPayload = (DreRequestPayload) requestTask.getRequest().getPayload();
		requestPayload.setDecisionNotification(decisionNotification);
		updateRequestPayloadWithSubmitRequestTaskPayload(requestPayload, requestTaskPayload);
	}
	
	@Transactional
	public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
		final DreRequestPayload requestPayload = (DreRequestPayload) requestTask.getRequest().getPayload();
		final DreApplicationSubmitRequestTaskPayload requestTaskPayload = (DreApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		
		requestPayload.setRegulatorPeerReviewer(peerReviewer);
		requestPayload.setRegulatorReviewer(appUser.getUserId());
		updateRequestPayloadWithSubmitRequestTaskPayload(requestPayload, requestTaskPayload);
	}
	
	private void updateRequestPayloadWithSubmitRequestTaskPayload(final DreRequestPayload requestPayload,
			final DreApplicationSubmitRequestTaskPayload requestTaskPayload) {
		final Dre dre = requestTaskPayload.getDre();
		requestPayload.setDre(dre);
		requestPayload.setSectionCompleted(requestTaskPayload.isSectionCompleted());
		requestPayload.setDreAttachments(requestTaskPayload.getDreAttachments());
	}

}
