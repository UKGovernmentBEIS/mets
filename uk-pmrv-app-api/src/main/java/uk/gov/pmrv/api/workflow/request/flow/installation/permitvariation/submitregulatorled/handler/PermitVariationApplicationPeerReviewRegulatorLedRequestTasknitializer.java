package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper.PermitVariationRegulatorLedMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationPeerReviewRegulatorLedRequestTasknitializer implements InitializeRequestTaskHandler {

	private static final PermitVariationRegulatorLedMapper PERMIT_VARIATION_MAPPER = Mappers.getMapper(PermitVariationRegulatorLedMapper.class);
	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

	@Override
	public RequestTaskPayload initializePayload(final Request request) {
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

		final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = PERMIT_VARIATION_MAPPER
				.toPermitVariationApplicationSubmitRegulatorLedRequestTaskPayload(requestPayload,
						RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD);

		final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
				.getInstallationOperatorDetails(request.getAccountId());
		taskPayload.setInstallationOperatorDetails(installationOperatorDetails);

		return taskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW);
	}
}
