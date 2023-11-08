package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.validator.EmpVariationCorsiaDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.mapper.EmpVariationCorsiaSubmitMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaSubmitService {

	private final RequestService requestService;
	private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private final EmpVariationCorsiaDetailsValidator empVariationCorsiaDetailsValidator;
	
	private static final EmpVariationCorsiaMapper EMP_VARIATION_CORSIA_MAPPER = 
			Mappers.getMapper(EmpVariationCorsiaMapper.class);
	private static final EmpVariationCorsiaSubmitMapper EMP_VARIATION_CORSIA_SUBMIT_MAPPER = 
					Mappers.getMapper(EmpVariationCorsiaSubmitMapper.class);
	
	@Transactional
	public void saveEmpVariation(
			EmpVariationCorsiaSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		EmpVariationCorsiaApplicationSubmitRequestTaskPayload taskPayload = (EmpVariationCorsiaApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		taskPayload.setEmissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan());
		taskPayload.setEmpVariationDetails(taskActionPayload.getEmpVariationDetails());
		taskPayload.setEmpVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted());
		taskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());
		taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
	}
	
	@Transactional
	public void submitEmpVariation(RequestTask requestTask, PmrvUser authUser) {
		EmpVariationCorsiaApplicationSubmitRequestTaskPayload taskPayload = (EmpVariationCorsiaApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		Request request = requestTask.getRequest();
		
		EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer =
				EMP_VARIATION_CORSIA_MAPPER.toEmissionsMonitoringPlanCorsiaContainer(taskPayload);
		
		//validate EMP
		empCorsiaValidatorService.validateEmissionsMonitoringPlan(empCorsiaContainer);
		//validate EMP variation details
		empVariationCorsiaDetailsValidator.validate(taskPayload.getEmpVariationDetails());
		
		//save EMP to request payload
		EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
		requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
		requestPayload.setEmpVariationDetails(taskPayload.getEmpVariationDetails());
		requestPayload.setEmpVariationDetailsCompleted(taskPayload.getEmpVariationDetailsCompleted());
		requestPayload.setEmpSectionsCompleted(taskPayload.getEmpSectionsCompleted());
		requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
		requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
		
		//add request action
		addEmpVariationCorsiaApplicationSubmittedRequestAction(authUser, taskPayload, request);
	}

	private void addEmpVariationCorsiaApplicationSubmittedRequestAction(PmrvUser authUser,
			EmpVariationCorsiaApplicationSubmitRequestTaskPayload taskPayload, Request request) {		
		RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		EmpVariationCorsiaApplicationSubmittedRequestActionPayload actionPayload = EMP_VARIATION_CORSIA_SUBMIT_MAPPER
				.toEmpVariationCorsiaApplicationSubmittedRequestActionPayload(taskPayload, accountInfo);
		
        requestService.addActionToRequest(request, actionPayload, RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED, authUser.getUserId());
	}
}
