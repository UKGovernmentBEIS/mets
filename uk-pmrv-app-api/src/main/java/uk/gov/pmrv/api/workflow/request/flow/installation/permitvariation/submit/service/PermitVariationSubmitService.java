package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationDetailsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.mapper.PermitVariationSubmitMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationSubmitService {

	private final RequestService requestService;
	private final PermitValidatorService permitValidatorService;
    private final PermitVariationDetailsValidator permitVariationDetailsValidator;
	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private final PermitVariationSubmitMapper permitVariationMapper = Mappers.getMapper(PermitVariationSubmitMapper.class);
	
	@Transactional
	public void savePermitVariation(
			PermitVariationSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
		PermitVariationApplicationSubmitRequestTaskPayload taskPayload = (PermitVariationApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		taskPayload.setPermitVariationDetails(taskActionPayload.getPermitVariationDetails());
		taskPayload.setPermitVariationDetailsCompleted(taskActionPayload.getPermitVariationDetailsCompleted());
		taskPayload.setPermit(taskActionPayload.getPermit());
		taskPayload.setPermitSectionsCompleted(taskActionPayload.getPermitSectionsCompleted());
		taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
	}
	
	@Transactional
	public void submitPermitVariation(RequestTask requestTask, AppUser authUser) {
		PermitVariationApplicationSubmitRequestTaskPayload taskPayload = (PermitVariationApplicationSubmitRequestTaskPayload) requestTask
				.getPayload();
		final Request request = requestTask.getRequest();
		final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
				.getInstallationOperatorDetails(request.getAccountId());
		
		final PermitContainer permitContainer = permitVariationMapper.toPermitContainer(taskPayload, installationOperatorDetails);
		
		//validate permit
		permitValidatorService.validatePermit(permitContainer);
		permitVariationDetailsValidator.validate(taskPayload.getPermitVariationDetails());
		
		//save permit to request payload
		PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
		requestPayload.setPermitVariationDetails(taskPayload.getPermitVariationDetails());
		requestPayload.setPermitVariationDetailsCompleted(taskPayload.getPermitVariationDetailsCompleted());
        requestPayload.setPermitType(taskPayload.getPermitType());
		requestPayload.setPermit(taskPayload.getPermit());
		requestPayload.setPermitSectionsCompleted(taskPayload.getPermitSectionsCompleted());
		requestPayload.setPermitAttachments(taskPayload.getPermitAttachments());
		requestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());

		//add request action
		PermitVariationApplicationSubmittedRequestActionPayload actionPayload = permitVariationMapper
				.toPermitVariationApplicationSubmittedRequestActionPayload(taskPayload, installationOperatorDetails);
        requestService.addActionToRequest(request, actionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_SUBMITTED, authUser.getUserId());
	}
}
