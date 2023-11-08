package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils.EmpUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper.EmpUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation.EmpIssuanceUkEtsReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestEmpUkEtsReviewService {

    private final EmpIssuanceUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpUkEtsReviewMapper EMP_UK_ETS_REVIEW_MAPPER = Mappers.getMapper(EmpUkEtsReviewMapper.class);

    @Transactional
    public void applySaveAction(EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload empIssuanceReviewRequestTaskActionPayload,
                                RequestTask requestTask) {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload
            empIssuanceApplicationReviewRequestTaskPayload = (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        //remove review group decisions for dynamic sections that do not exist any more
        Set<EmpUkEtsReviewGroup> deprecatedReviewGroups = EmpUkEtsReviewUtils.getDeprecatedReviewGroupDecisions(
        		empIssuanceApplicationReviewRequestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(), 
        		empIssuanceReviewRequestTaskActionPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
        	empIssuanceApplicationReviewRequestTaskPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }

        resetDeterminationIfApproved(empIssuanceApplicationReviewRequestTaskPayload);

        updateEmpReviewRequestTaskPayload(empIssuanceApplicationReviewRequestTaskPayload, empIssuanceReviewRequestTaskActionPayload);
    }

    @Transactional
    public void saveReviewGroupDecision(EmpIssuanceUkEtsSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                        RequestTask requestTask) {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        applicationReviewRequestTaskPayload.getReviewGroupDecisions().put(taskActionPayload.getReviewGroup(), taskActionPayload.getDecision());
        applicationReviewRequestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        applicationReviewRequestTaskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());

        resetDeterminationIfNotValidWithDecisions(applicationReviewRequestTaskPayload);
    }

    @Transactional
    public void saveDetermination(EmpIssuanceUkEtsSaveReviewDeterminationRequestTaskActionPayload taskActionPayload,
                                  RequestTask requestTask) {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDetermination(taskActionPayload.getDetermination());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void saveDecisionNotification(RequestTask requestTask, DecisionNotification decisionNotification,
                                         PmrvUser pmrvUser) {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpIssuanceUkEtsRequestPayload empIssuanceRequestPayload =
            (EmpIssuanceUkEtsRequestPayload) requestTask.getRequest().getPayload();

        updateEmpIssuanceRequestPayload(empIssuanceRequestPayload, taskPayload, pmrvUser);
        empIssuanceRequestPayload.setDecisionNotification(decisionNotification);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReviewer, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpIssuanceUkEtsRequestPayload empIssuanceRequestPayload =
            (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        updateEmpIssuanceRequestPayload(empIssuanceRequestPayload, reviewRequestTaskPayload, pmrvUser);
        empIssuanceRequestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }
    
    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpIssuanceUkEtsRequestPayload empIssuanceRequestPayload =
            (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        updateEmpIssuanceRequestPayload(empIssuanceRequestPayload, taskPayload, pmrvUser);
    }

    @Transactional
    public void saveAmend(EmpIssuanceUkEtsSaveApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        updateEmpReviewRequestTaskPayload(taskPayload, actionPayload);
    }

    @Transactional
    public void submitAmend(EmpIssuanceUkEtsSubmitApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask,
                            PmrvUser pmrvUser) {

        Request request = requestTask.getRequest();
        EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate monitoring plan
        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer = EMP_UK_ETS_REVIEW_MAPPER
                .toEmissionsMonitoringPlanUkEtsContainer(taskPayload);
        empUkEtsValidatorService.validateEmissionsMonitoringPlan(empUkEtsContainer);

        // Update request payload
        EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the emp
        //this should be done before setting the EmissionsMonitoringPlan object of the requestPayload with the corresponding from the task payload
        removeDeprecatedReviewGroupDecisionsFromEmpIssuanceRequestPayload(requestPayload, taskPayload);

        requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
        requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setEmpSectionsCompleted(actionPayload.getEmpSectionsCompleted());

        // Add timeline
        addAmendsSubmittedRequestAction(request, pmrvUser);
    }

    private void updateEmpIssuanceRequestPayload(EmpIssuanceUkEtsRequestPayload empIssuanceRequestPayload,
                                                 EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload,
                                                 PmrvUser pmrvUser) {
        empIssuanceRequestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        empIssuanceRequestPayload.setEmissionsMonitoringPlan(reviewRequestTaskPayload.getEmissionsMonitoringPlan());
        empIssuanceRequestPayload.setEmpSectionsCompleted(reviewRequestTaskPayload.getEmpSectionsCompleted());
        empIssuanceRequestPayload.setEmpAttachments(reviewRequestTaskPayload.getEmpAttachments());
        empIssuanceRequestPayload.setReviewSectionsCompleted(reviewRequestTaskPayload.getReviewSectionsCompleted());
        empIssuanceRequestPayload.setReviewGroupDecisions(reviewRequestTaskPayload.getReviewGroupDecisions());
        empIssuanceRequestPayload.setReviewAttachments(reviewRequestTaskPayload.getReviewAttachments());
        empIssuanceRequestPayload.setDetermination(reviewRequestTaskPayload.getDetermination());
    }

    private void resetDeterminationIfApproved(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpIssuanceDetermination determination = reviewRequestTaskPayload.getDetermination();
        if (determination != null && EmpIssuanceDeterminationType.APPROVED == determination.getType()) {
            reviewRequestTaskPayload.setDetermination(null);
        }
    }

    private void resetDeterminationIfNotValidWithDecisions(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpIssuanceDetermination determination = reviewRequestTaskPayload.getDetermination();
        if (determination != null && !reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, determination.getType())) {
            reviewRequestTaskPayload.setDetermination(null);
        }
    }

    private void updateEmpReviewRequestTaskPayload(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload,
                                                   EmpIssuanceUkEtsSaveApplicationReviewRequestTaskActionPayload actionPayload) {
        taskPayload.setEmissionsMonitoringPlan(actionPayload.getEmissionsMonitoringPlan());
        taskPayload.setEmpSectionsCompleted(actionPayload.getEmpSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
    }

    private void removeDeprecatedReviewGroupDecisionsFromEmpIssuanceRequestPayload(EmpIssuanceUkEtsRequestPayload requestPayload,
                                                                                   EmpIssuanceUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {
        Set<EmpUkEtsReviewGroup> deprecatedReviewGroups = EmpUkEtsReviewUtils.getDeprecatedReviewGroupDecisions(
            requestPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(),
            amendsSubmitRequestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }
    }

    private void addAmendsSubmittedRequestAction(Request request, PmrvUser pmrvUser) {
        EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
                EMP_UK_ETS_REVIEW_MAPPER.toEmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload(requestPayload, accountInfo);

        requestService.addActionToRequest(
                request,
                amendsSubmittedRequestActionPayload,
                RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED,
                pmrvUser.getUserId());
    }
}
