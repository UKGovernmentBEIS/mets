package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper.PermitVariationRegulatorLedMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationSubmitRegulatorLedRequestTaskInitializer implements InitializeRequestTaskHandler {

	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private final PermitQueryService permitQueryService;
	private static final PermitVariationRegulatorLedMapper PERMIT_VARIATION_MAPPER = Mappers.getMapper(PermitVariationRegulatorLedMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

		final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
				.getInstallationOperatorDetails(request.getAccountId());
		
		final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload;
		
		if(isAlreadyDeterminated(requestPayload)) {
			requestTaskPayload = PERMIT_VARIATION_MAPPER
					.toPermitVariationApplicationSubmitRegulatorLedRequestTaskPayload(requestPayload,
							RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD);
			requestTaskPayload.setInstallationOperatorDetails(installationOperatorDetails);
			
		} else {
			final PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(request.getAccountId());
			requestTaskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
					.payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_REGULATOR_LED_PAYLOAD)
					.installationOperatorDetails(installationOperatorDetails)
					.determination(PermitVariationRegulatorLedGrantDetermination.builder()
							.annualEmissionsTargets(permitContainer.getAnnualEmissionsTargets()).build())
					.originalPermitContainer(permitContainer)
					.permitType(permitContainer.getPermitType())
					.permit(permitContainer.getPermit())
					.permitAttachments(permitContainer.getPermitAttachments()).build();
		}
		
		return requestTaskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT);
	}
	
	/**
	 * Is already determinated case occurs when the peer reviewer returns back the task to the main regulator  
	 */
	private boolean isAlreadyDeterminated(PermitVariationRequestPayload requestPayload) {
		return requestPayload.getDeterminationRegulatorLed() != null;
	}

}
