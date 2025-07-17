package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.validator.EmpVariationUkEtsDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.mapper.EmpVariationUkEtsSubmitMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsSubmitService {

	private final RequestService requestService;
	private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
	private final EmpVariationUkEtsDetailsValidator empVariationUkEtsDetailsValidator;
	private final RequestAviationAccountQueryService requestAviationAccountQueryService;
	private static final EmpVariationUkEtsMapper EMP_VARIATION_UKETS_MAPPER = Mappers.getMapper(EmpVariationUkEtsMapper.class);
	private static final EmpVariationUkEtsSubmitMapper EMP_VARIATION_UKETS_SUBMIT_MAPPER = Mappers.getMapper(EmpVariationUkEtsSubmitMapper.class);
	
	@Transactional
	public void saveEmpVariation(
			EmpVariationUkEtsSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload = (EmpVariationUkEtsApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		taskPayload.setEmissionsMonitoringPlan(taskActionPayload.getEmissionsMonitoringPlan());
		taskPayload.setEmpVariationDetails(taskActionPayload.getEmpVariationDetails());
		taskPayload.setEmpVariationDetailsCompleted(taskActionPayload.getEmpVariationDetailsCompleted());
		taskPayload.setEmpSectionsCompleted(taskActionPayload.getEmpSectionsCompleted());
		taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
	}
	
	@Transactional
	public void submitEmpVariation(RequestTask requestTask, AppUser authUser) {
		EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload = (EmpVariationUkEtsApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		Request request = requestTask.getRequest();
		
		EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer =
				EMP_VARIATION_UKETS_MAPPER.toEmissionsMonitoringPlanUkEtsContainer(taskPayload);
		
		//validate EMP
		empUkEtsValidatorService.validateEmissionsMonitoringPlan(empUkEtsContainer);
		//validate EMP variation details
		empVariationUkEtsDetailsValidator.validate(taskPayload.getEmpVariationDetails());
		
		//save EMP to request payload
		EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
		requestPayload.setEmissionsMonitoringPlan(taskPayload.getEmissionsMonitoringPlan());
		requestPayload.setEmpVariationDetails(taskPayload.getEmpVariationDetails());
		requestPayload.setEmpVariationDetailsCompleted(taskPayload.getEmpVariationDetailsCompleted());
		requestPayload.setEmpSectionsCompleted(taskPayload.getEmpSectionsCompleted());
		requestPayload.setEmpAttachments(taskPayload.getEmpAttachments());
		requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
		
		//add request action
		addEmpVariationUkEtsApplicationSubmittedRequestAction(authUser, taskPayload, request);
	}

	private void addEmpVariationUkEtsApplicationSubmittedRequestAction(AppUser authUser,
			EmpVariationUkEtsApplicationSubmitRequestTaskPayload taskPayload, Request request) {		
		RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());
		
		EmpVariationUkEtsApplicationSubmittedRequestActionPayload actionPayload = EMP_VARIATION_UKETS_SUBMIT_MAPPER
				.toEmpVariationUkEtsApplicationSubmittedRequestActionPayload(taskPayload, accountInfo);
		
        requestService.addActionToRequest(request, actionPayload, RequestActionType.EMP_VARIATION_UKETS_APPLICATION_SUBMITTED, authUser.getUserId());
	}
}
