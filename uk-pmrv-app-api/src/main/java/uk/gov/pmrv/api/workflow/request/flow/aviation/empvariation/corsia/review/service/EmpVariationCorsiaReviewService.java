package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils.EmpCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewService {

	private final EmpVariationCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;	
	
	@Transactional
    public void saveEmpVariation(EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload taskActionPayload,
                                    RequestTask requestTask) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload
            requestTaskPayload = (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        //remove review group decisions for dynamic sections that do not exist any more
        Set<EmpCorsiaReviewGroup> deprecatedReviewGroups = EmpCorsiaReviewUtils.getDeprecatedReviewGroupDecisions(
        		requestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(), 
        		taskActionPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
        	requestTaskPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }
        
        resetDeterminationIfNotDeemedWithdrawn(requestTaskPayload);
               
        updateRequestTaskPayload(taskActionPayload, requestTaskPayload);
    }

	@Transactional
    public void saveReviewGroupDecision(EmpVariationCorsiaSaveReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload
            taskPayload = (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final EmpCorsiaReviewGroup group = payload.getGroup();
        final EmpVariationReviewDecision decision = payload.getDecision();

        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();
        reviewGroupDecisions.put(group, decision);
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        taskPayload.setEmpSectionsCompleted(payload.getEmpSectionsCompleted());
        
        resetDeterminationIfNotValidWithDecisions(taskPayload);
    }

	@Transactional
    public void saveDetailsReviewGroupDecision(EmpVariationCorsiaSaveDetailsReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload
            taskPayload = (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setEmpVariationDetailsReviewDecision(payload.getDecision());  
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        taskPayload.setEmpVariationDetailsReviewCompleted(payload.getEmpVariationDetailsReviewCompleted());
        taskPayload.setEmpVariationDetailsCompleted(payload.getEmpVariationDetailsCompleted());      
        
        resetDeterminationIfNotValidWithDecisions(taskPayload);
    }
	
	@Transactional
    public void saveDetermination(EmpVariationCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload,
                                  RequestTask requestTask) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDetermination(taskActionPayload.getDetermination());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }
	
	@Transactional
    public void saveDecisionNotification(RequestTask requestTask, DecisionNotification decisionNotification,
                                         AppUser appUser) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpVariationCorsiaRequestPayload requestPayload =
            (EmpVariationCorsiaRequestPayload) requestTask.getRequest().getPayload();

        updateRequestPayload(requestPayload, taskPayload, appUser);
        requestPayload.setDecisionNotification(decisionNotification);
	}
	
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReviewer, AppUser appUser) {
        Request request = requestTask.getRequest();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpVariationCorsiaRequestPayload requestPayload =
            (EmpVariationCorsiaRequestPayload) request.getPayload();

        updateRequestPayload(requestPayload, reviewRequestTaskPayload, appUser);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }

    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpVariationCorsiaRequestPayload requestPayload =
            (EmpVariationCorsiaRequestPayload) request.getPayload();

        updateRequestPayload(requestPayload, taskPayload, appUser);
    }
	
	private void updateRequestTaskPayload(
			EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload taskActionPayload,
			EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
    	
		requestTaskPayload.setEmpVariationDetails(taskActionPayload.getEmpVariationDetails());
        requestTaskPayload.setEmpVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted());
        requestTaskPayload.setEmissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan());
        requestTaskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());
        requestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestTaskPayload.setEmpVariationDetailsReviewCompleted(taskActionPayload.getEmpVariationDetailsReviewCompleted());
	}
	
	private void resetDeterminationIfNotDeemedWithdrawn(
			EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {	
    	EmpVariationDetermination determination = requestTaskPayload.getDetermination();
        if (determination != null && EmpVariationDeterminationType.DEEMED_WITHDRAWN != determination.getType()) {
        	requestTaskPayload.setDetermination(null);
        }	
	}
    
    private void resetDeterminationIfNotValidWithDecisions(
			EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload) {	
    	EmpVariationDetermination determination = taskPayload.getDetermination();
        if (determination != null && !reviewDeterminationValidatorService.isValid(taskPayload, determination.getType())) {
        	taskPayload.setDetermination(null);
        }	
	}
    
    private void updateRequestPayload(EmpVariationCorsiaRequestPayload requestPayload,
            EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload,
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
