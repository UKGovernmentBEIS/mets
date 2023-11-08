package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.service;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.utils.EmpUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper.EmpVariationUkEtsReviewMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsAmendService {

	private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
	private final RequestService requestService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	
	private static final EmpVariationUkEtsReviewMapper MAPPER = Mappers.getMapper(EmpVariationUkEtsReviewMapper.class);
	
	@Transactional
    public void saveAmend(EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setEmpVariationDetails(actionPayload.getEmpVariationDetails());
        taskPayload.setEmpVariationDetailsCompleted(actionPayload.getEmpVariationDetailsCompleted());
        taskPayload.setEmissionsMonitoringPlan(actionPayload.getEmissionsMonitoringPlan());
        taskPayload.setEmpSectionsCompleted(actionPayload.getEmpSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
        taskPayload.setEmpVariationDetailsReviewCompleted(actionPayload.getEmpVariationDetailsReviewCompleted());
        taskPayload.setEmpVariationDetailsAmendCompleted(actionPayload.getEmpVariationDetailsAmendCompleted());
    }
    
    @Transactional
    public void submitAmend(EmpVariationUkEtsSubmitApplicationAmendRequestTaskActionPayload actionPayload, RequestTask requestTask,
                            PmrvUser pmrvUser) {

        Request request = requestTask.getRequest();
        EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate monitoring plan
        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer = MAPPER.toEmissionsMonitoringPlanUkEtsContainer(taskPayload);
        empUkEtsValidatorService.validateEmissionsMonitoringPlan(empUkEtsContainer);

        // Update request payload
        EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();

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

    private void removeDeprecatedReviewGroupDecisionsFromVariationRequestPayload(EmpVariationUkEtsRequestPayload requestPayload,
                                                                                 EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {
        Set<EmpUkEtsReviewGroup> deprecatedReviewGroups = EmpUkEtsReviewUtils.getDeprecatedReviewGroupDecisions(
            requestPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections(),
            amendsSubmitRequestTaskPayload.getEmissionsMonitoringPlan().getNotEmptyDynamicSections());
        if (!deprecatedReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedReviewGroups);
        }
    }
    
    private void addAmendsSubmittedRequestAction(Request request, PmrvUser pmrvUser) {
    	EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload amendsSubmittedRequestActionPayload =
                MAPPER.toEmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload(requestPayload, accountInfo);
        
        requestService.addActionToRequest(
                request,
                amendsSubmittedRequestActionPayload,
                RequestActionType.EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED,
                pmrvUser.getUserId());
    }
}
