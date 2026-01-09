package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.mapper.PermitVariationMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationUpdatePermitService {
	
	private final RequestService requestService;
	private final PermitService permitService;
	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private final InstallationAccountUpdateService installationAccountUpdateService;
	private static final PermitVariationMapper PERMIT_VARIATION_MAPPER = Mappers.getMapper(PermitVariationMapper.class);
	
	@Transactional
	public void updatePermitUponGrantedDetermination(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final Long accountId = request.getAccountId();
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
                request.getAccountId());
        final PermitContainer permitContainer = PERMIT_VARIATION_MAPPER.toPermitContainer(requestPayload, installationOperatorDetails);

		EmitterType emitterType = switch (permitContainer.getPermitType()) {
			case HSE -> EmitterType.HSE;
			case WASTE -> EmitterType.WASTE;
			default -> EmitterType.GHGE;
		};
        
        permitService.updatePermit(permitContainer, accountId);
		installationAccountUpdateService.updateAccountUponPermitVariationGranted(
				accountId,
				emitterType,
				permitContainer.getPermit().getEstimatedAnnualEmissions().getQuantity());
	}
	
}
