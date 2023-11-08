package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils.EmpCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper.EmpCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation.EmpIssuanceCorsiaReviewDeterminationValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class RequestEmpCorsiaReviewService {

    private final EmpIssuanceCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;
    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpCorsiaReviewMapper EMP_CORSIA_REVIEW_MAPPER = Mappers.getMapper(EmpCorsiaReviewMapper.class);


    @Transactional
    public void applySaveAction(
        EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload empIssuanceReviewRequestTaskActionPayload,
        RequestTask requestTask) {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload
            empIssuanceApplicationReviewRequestTaskPayload = (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        //remove review group decisions for dynamic sections that do not exist any more
        Set<EmpCorsiaReviewGroup> deprecatedReviewGroups = EmpCorsiaReviewUtils.getDeprecatedReviewGroupDecisions(
            empIssuanceApplicationReviewRequestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(),
            empIssuanceReviewRequestTaskActionPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
            empIssuanceApplicationReviewRequestTaskPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }

        resetDeterminationIfApproved(empIssuanceApplicationReviewRequestTaskPayload);

        updateEmpReviewRequestTaskPayload(empIssuanceApplicationReviewRequestTaskPayload, empIssuanceReviewRequestTaskActionPayload);
    }

    @Transactional
    public void saveReviewGroupDecision(
        EmpIssuanceCorsiaSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        applicationReviewRequestTaskPayload.getReviewGroupDecisions().put(taskActionPayload.getReviewGroup(), taskActionPayload.getDecision());
        applicationReviewRequestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        applicationReviewRequestTaskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());

        resetDeterminationIfNotValidWithDecisions(applicationReviewRequestTaskPayload);
    }

    @Transactional
    public void saveDetermination(EmpIssuanceCorsiaSaveReviewDeterminationRequestTaskActionPayload taskActionPayload,
                                  RequestTask requestTask) {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDetermination(taskActionPayload.getDetermination());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void saveDecisionNotification(RequestTask requestTask, DecisionNotification decisionNotification,
                                         PmrvUser pmrvUser) {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpIssuanceCorsiaRequestPayload empIssuanceRequestPayload =
            (EmpIssuanceCorsiaRequestPayload) requestTask.getRequest().getPayload();

        updateEmpIssuanceRequestPayload(empIssuanceRequestPayload, taskPayload, pmrvUser);
        empIssuanceRequestPayload.setDecisionNotification(decisionNotification);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReviewer, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpIssuanceCorsiaRequestPayload empIssuanceRequestPayload =
            (EmpIssuanceCorsiaRequestPayload) request.getPayload();

        updateEmpIssuanceRequestPayload(empIssuanceRequestPayload, reviewRequestTaskPayload, pmrvUser);
        empIssuanceRequestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }

    private void updateEmpIssuanceRequestPayload(EmpIssuanceCorsiaRequestPayload empIssuanceRequestPayload,
                                                 EmpIssuanceCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload,
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

    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        EmpIssuanceCorsiaRequestPayload empIssuanceRequestPayload =
            (EmpIssuanceCorsiaRequestPayload) request.getPayload();

        updateEmpIssuanceRequestPayload(empIssuanceRequestPayload, taskPayload, pmrvUser);
    }

    @Transactional
    public void saveAmend(EmpIssuanceCorsiaSaveApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        updateEmpReviewRequestTaskPayload(taskPayload, actionPayload);
    }

    @Transactional
    public void submitAmend(EmpIssuanceCorsiaSubmitApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask,
                            PmrvUser pmrvUser) {

        Request request = requestTask.getRequest();
        EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate monitoring plan
        EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer = EMP_CORSIA_REVIEW_MAPPER
            .toEmissionsMonitoringPlanCorsiaContainer(taskPayload);
        empCorsiaValidatorService.validateEmissionsMonitoringPlan(empCorsiaContainer);

        // Update request payload
        EmpIssuanceCorsiaRequestPayload requestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();

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

    private void resetDeterminationIfApproved(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpIssuanceDetermination determination = reviewRequestTaskPayload.getDetermination();
        if (determination != null && EmpIssuanceDeterminationType.APPROVED == determination.getType()) {
            reviewRequestTaskPayload.setDetermination(null);
        }
    }

    private void resetDeterminationIfNotValidWithDecisions(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload) {
        EmpIssuanceDetermination determination = reviewRequestTaskPayload.getDetermination();
        if (determination != null && !reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, determination.getType())) {
            reviewRequestTaskPayload.setDetermination(null);
        }
    }

    private void updateEmpReviewRequestTaskPayload(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload,
                                                   EmpIssuanceCorsiaSaveApplicationReviewRequestTaskActionPayload actionPayload) {
        taskPayload.setEmissionsMonitoringPlan(actionPayload.getEmissionsMonitoringPlan());
        taskPayload.setEmpSectionsCompleted(actionPayload.getEmpSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
    }

    private void removeDeprecatedReviewGroupDecisionsFromEmpIssuanceRequestPayload(
        EmpIssuanceCorsiaRequestPayload requestPayload,
        EmpIssuanceCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {
        Set<EmpCorsiaReviewGroup> deprecatedReviewGroups = EmpCorsiaReviewUtils.getDeprecatedReviewGroupDecisions(
            requestPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(),
            amendsSubmitRequestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }
    }

    private void addAmendsSubmittedRequestAction(Request request, PmrvUser pmrvUser) {
        EmpIssuanceCorsiaRequestPayload requestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
            EMP_CORSIA_REVIEW_MAPPER.toEmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload(requestPayload, accountInfo);

        requestService.addActionToRequest(
            request,
            amendsSubmittedRequestActionPayload,
            RequestActionType.EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED,
            pmrvUser.getUserId());
    }
}