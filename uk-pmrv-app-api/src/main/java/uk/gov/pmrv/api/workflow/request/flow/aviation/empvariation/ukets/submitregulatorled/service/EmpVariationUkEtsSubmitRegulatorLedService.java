package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsSubmitRegulatorLedService {

	@Transactional
	public void saveEmpVariation(
			EmpVariationUkEtsSaveApplicationRegulatorLedRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = (EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask
				.getPayload();
		
		taskPayload.setEmissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan());
		taskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());
		taskPayload.setEmpVariationDetails(taskActionPayload.getEmpVariationDetails());
		taskPayload.setEmpVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted());
		taskPayload.setReasonRegulatorLed(taskActionPayload.getReasonRegulatorLed());
	}
	
	@Transactional
	public void saveReviewGroupDecision(
			EmpVariationUkEtsSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload requestTaskActionPayload,
			RequestTask requestTask) {
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload
            requestTaskPayload = (EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
		
		requestTaskPayload.setEmpSectionsCompleted(requestTaskActionPayload.getEmpSectionsCompleted());
    	
        Map<EmpUkEtsReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisions = requestTaskPayload.getReviewGroupDecisions();
        reviewGroupDecisions.put(requestTaskActionPayload.getGroup(), requestTaskActionPayload.getDecision());
    }
	
	@Transactional
	public void saveDecisionNotification(RequestTask requestTask, DecisionNotification decisionNotification,
			AppUser appUser) {
		EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = (EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask
				.getPayload();

		EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) requestTask.getRequest()
				.getPayload();

		requestPayload.setRegulatorReviewer(appUser.getUserId());
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
