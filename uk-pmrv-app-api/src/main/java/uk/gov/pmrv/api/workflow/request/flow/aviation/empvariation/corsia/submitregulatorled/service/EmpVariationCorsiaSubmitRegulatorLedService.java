package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaSubmitRegulatorLedService {

	@Transactional
	public void saveEmpVariation(
		final EmpVariationCorsiaSaveApplicationRegulatorLedRequestTaskActionPayload taskActionPayload,
		final RequestTask requestTask
	) {
		
		final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = 
			(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
		
		taskPayload.setEmissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan());
		taskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());
		taskPayload.setEmpVariationDetails(taskActionPayload.getEmpVariationDetails());
		taskPayload.setEmpVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted());
		taskPayload.setReasonRegulatorLed(taskActionPayload.getReasonRegulatorLed());
	}

	@Transactional
	public void saveReviewGroupDecision(
		final EmpVariationCorsiaSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload requestTaskActionPayload,
		final RequestTask requestTask
	) {

		final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload =
			(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

		requestTaskPayload.setEmpSectionsCompleted(requestTaskActionPayload.getEmpSectionsCompleted());
		requestTaskPayload.getReviewGroupDecisions().put(requestTaskActionPayload.getGroup(), requestTaskActionPayload.getDecision());
	}

	@Transactional
	public void saveDecisionNotification(final RequestTask requestTask, 
										 final DecisionNotification decisionNotification,
										 final PmrvUser pmrvUser) {
		
		final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = 
			(EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();

		final EmpVariationCorsiaRequestPayload requestPayload = 
			(EmpVariationCorsiaRequestPayload) requestTask.getRequest().getPayload();

		requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
		requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
		requestPayload.setEmpVariationDetails(taskPayload.getEmpVariationDetails());
		requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
		requestPayload.setEmpSectionsCompleted(taskPayload.getEmpSectionsCompleted());
		requestPayload.setEmpVariationDetailsCompleted(taskPayload.getEmpVariationDetailsCompleted());
		requestPayload.setReviewGroupDecisionsRegulatorLed(taskPayload.getReviewGroupDecisions());
		requestPayload.setReasonRegulatorLed(taskPayload.getReasonRegulatorLed());

		requestPayload.setDecisionNotification(decisionNotification);
	}
}
