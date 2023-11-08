package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.service;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.utils.EmpCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper.EmpVariationCorsiaReviewMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaAmendService {

	private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;
	private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;

    private static final EmpVariationCorsiaReviewMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaReviewMapper.class);
	
	@Transactional
    public void saveAmend(EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setEmpVariationDetails(actionPayload.getEmpVariationDetails());
        taskPayload.setEmpVariationDetailsCompleted(actionPayload.getEmpVariationDetailsCompleted());
        taskPayload.setEmissionsMonitoringPlan(actionPayload.getEmissionsMonitoringPlan());
        taskPayload.setEmpSectionsCompleted(actionPayload.getEmpSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
        taskPayload.setEmpVariationDetailsReviewCompleted(actionPayload.getEmpVariationDetailsReviewCompleted());
        taskPayload.setEmpVariationDetailsAmendCompleted(actionPayload.getEmpVariationDetailsAmendCompleted());
    }
    
    @Transactional
    public void submitAmend(EmpVariationCorsiaSubmitApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask,
                            PmrvUser pmrvUser) {

        Request request = requestTask.getRequest();
        EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate monitoring plan
        EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer = MAPPER.toEmissionsMonitoringPlanCorsiaContainer(taskPayload);
        empCorsiaValidatorService.validateEmissionsMonitoringPlan(empCorsiaContainer);

        // Update request payload
        EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the emp
        //this should be done before setting the EmissionsMonitoringPlan object of the requestPayload with the corresponding from the task payload
        removeDeprecatedReviewGroupDecisionsFromVariationRequestPayload(requestPayload, taskPayload);

        requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
        requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
        requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        requestPayload.setEmpSectionsCompleted(actionPayload.getEmpSectionsCompleted());
        requestPayload.setEmpVariationDetails(taskPayload.getEmpVariationDetails());
        requestPayload.setEmpVariationDetailsCompleted(taskPayload.getEmpVariationDetailsCompleted());    
        requestPayload.setEmpVariationDetailsReviewCompleted(taskPayload.getEmpVariationDetailsReviewCompleted());
        
        // Add timeline
        addAmendsSubmittedRequestAction(request, pmrvUser);
    }

    private void removeDeprecatedReviewGroupDecisionsFromVariationRequestPayload(EmpVariationCorsiaRequestPayload requestPayload,
                                                                                 EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {
        Set<EmpCorsiaReviewGroup> deprecatedReviewGroups = EmpCorsiaReviewUtils.getDeprecatedReviewGroupDecisions(
            requestPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(),
            amendsSubmitRequestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }
    }
    
    private void addAmendsSubmittedRequestAction(Request request, PmrvUser pmrvUser) {
        EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
            MAPPER.toEmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload(requestPayload, accountInfo);

        requestService.addActionToRequest(
                request,
                amendsSubmittedRequestActionPayload,
                RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED,
                pmrvUser.getUserId());
    }
}
