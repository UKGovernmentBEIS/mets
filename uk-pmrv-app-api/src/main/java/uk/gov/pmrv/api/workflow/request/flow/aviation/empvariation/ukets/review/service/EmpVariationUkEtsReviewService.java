package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils.EmpUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewService {
	
	private final EmpVariationUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;	

    @Transactional
    public void saveEmpVariation(EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload taskActionPayload,
                                    RequestTask requestTask) {
        EmpVariationUkEtsApplicationReviewRequestTaskPayload
            requestTaskPayload = (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        //remove review group decisions for dynamic sections that do not exist any more
        Set<EmpUkEtsReviewGroup> deprecatedReviewGroups = EmpUkEtsReviewUtils.getDeprecatedReviewGroupDecisions(
        		requestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(), 
        		taskActionPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
        	requestTaskPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }
        
        resetDeterminationIfNotDeemedWithdrawn(requestTaskPayload);
        
        updateRequestTaskPayload(taskActionPayload, requestTaskPayload);
    }

	@Transactional
    public void saveReviewGroupDecision(EmpVariationUkEtsSaveReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        EmpVariationUkEtsApplicationReviewRequestTaskPayload
            taskPayload = (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final EmpUkEtsReviewGroup group = payload.getGroup();
        final EmpVariationReviewDecision decision = payload.getDecision();

        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();
        reviewGroupDecisions.put(group, decision);
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        taskPayload.setEmpSectionsCompleted(payload.getEmpSectionsCompleted());
        
        resetDeterminationIfNotValidWithDecisions(taskPayload);
    }

	@Transactional
    public void saveDetailsReviewGroupDecision(EmpVariationUkEtsSaveDetailsReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        EmpVariationUkEtsApplicationReviewRequestTaskPayload
            taskPayload = (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setEmpVariationDetailsReviewDecision(payload.getDecision());  
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        taskPayload.setEmpVariationDetailsReviewCompleted(payload.getEmpVariationDetailsReviewCompleted());
        taskPayload.setEmpVariationDetailsCompleted(payload.getEmpVariationDetailsCompleted());
        
        resetDeterminationIfNotValidWithDecisions(taskPayload);
    }
    
    @Transactional
    public void saveDetermination(EmpVariationUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload,
                                  RequestTask requestTask) {
        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDetermination(taskActionPayload.getDetermination());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Transactional
    public void saveDecisionNotification(RequestTask requestTask, DecisionNotification decisionNotification,
                                         AppUser appUser) {
        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpVariationUkEtsRequestPayload requestPayload =
            (EmpVariationUkEtsRequestPayload) requestTask.getRequest().getPayload();

        updateRequestPayload(requestPayload, taskPayload, appUser);
        requestPayload.setDecisionNotification(decisionNotification);
    }
    
    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReviewer, AppUser appUser) {
        Request request = requestTask.getRequest();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpVariationUkEtsRequestPayload requestPayload =
            (EmpVariationUkEtsRequestPayload) request.getPayload();

        updateRequestPayload(requestPayload, reviewRequestTaskPayload, appUser);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }
    
    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpVariationUkEtsRequestPayload requestPayload =
            (EmpVariationUkEtsRequestPayload) request.getPayload();

        updateRequestPayload(requestPayload, taskPayload, appUser);
    }
    
    private void updateRequestTaskPayload(
			EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload taskActionPayload,
			EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
    	
		requestTaskPayload.setEmpVariationDetails(taskActionPayload.getEmpVariationDetails());
        requestTaskPayload.setEmpVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted());
        requestTaskPayload.setEmissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan());
        requestTaskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());
        requestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestTaskPayload.setEmpVariationDetailsReviewCompleted(taskActionPayload.getEmpVariationDetailsReviewCompleted());
	}

    private void resetDeterminationIfNotDeemedWithdrawn(
			EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {	
    	EmpVariationDetermination determination = requestTaskPayload.getDetermination();
        if (determination != null && EmpVariationDeterminationType.DEEMED_WITHDRAWN != determination.getType()) {
        	requestTaskPayload.setDetermination(null);
        }	
	}
    
    private void resetDeterminationIfNotValidWithDecisions(
			EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload) {	
    	EmpVariationDetermination determination = taskPayload.getDetermination();
        if (determination != null && !reviewDeterminationValidatorService.isValid(taskPayload, determination.getType())) {
        	taskPayload.setDetermination(null);
        }	
	}
    
    private void updateRequestPayload(EmpVariationUkEtsRequestPayload requestPayload,
            EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload,
            AppUser appUser) {
    	
		requestPayload.setRegulatorReviewer(appUser.getUserId());
		requestPayload.setEmpVariationDetails(taskPayload.getEmpVariationDetails());
		requestPayload.setEmpVariationDetailsCompleted(taskPayload.getEmpVariationDetailsCompleted());
		requestPayload.setEmpVariationDetailsReviewCompleted(taskPayload.getEmpVariationDetailsReviewCompleted());
		requestPayload.setEmpVariationDetailsReviewDecision(taskPayload.getEmpVariationDetailsReviewDecision());
		requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
		requestPayload.setEmpSectionsCompleted(taskPayload.getEmpSectionsCompleted());
		requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
		requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
		requestPayload.setReviewGroupDecisions(taskPayload.getReviewGroupDecisions());
		requestPayload.setReviewAttachments(taskPayload.getReviewAttachments());
		requestPayload.setDetermination(taskPayload.getDetermination());
    }
}
