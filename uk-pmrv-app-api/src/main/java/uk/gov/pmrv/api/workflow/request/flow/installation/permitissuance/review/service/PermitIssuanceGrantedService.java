package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.mapper.PermitMapper;

@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedService {

    private static final PermitMapper PERMIT_MAPPER = Mappers.getMapper(PermitMapper.class);

    private final RequestService requestService;
    private final PermitService permitService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final InstallationAccountUpdateService installationAccountUpdateService;

    public void grant(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
            request.getAccountId());

        final PermitContainer permitContainer = PERMIT_MAPPER.toPermitContainer(requestPayload, installationOperatorDetails);
        permitService.submitPermit(permitContainer, accountId);

        EmitterType emitterType = switch (permitContainer.getPermitType()) {
            case HSE -> EmitterType.HSE;
            case WASTE -> EmitterType.WASTE;
            default -> EmitterType.GHGE;
        };

        installationAccountUpdateService.updateAccountUponPermitGranted(
            accountId,
            emitterType,
            permitContainer.getPermit().getEstimatedAnnualEmissions().getQuantity());
    }
}
