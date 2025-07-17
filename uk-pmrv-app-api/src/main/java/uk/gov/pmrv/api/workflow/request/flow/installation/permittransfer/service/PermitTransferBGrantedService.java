package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.mapper.PermitMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitTransferBGrantedService {

    private static final PermitMapper PERMIT_MAPPER = Mappers.getMapper(PermitMapper.class);

    private final RequestService requestService;
    private final PermitService permitService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final InstallationAccountUpdateService installationAccountUpdateService;

    public void grant(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        final InstallationOperatorDetails installationOperatorDetails = 
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());

        final PermitContainer permitContainer = PERMIT_MAPPER.toPermitContainer(requestPayload, installationOperatorDetails);
        permitService.submitPermit(permitContainer, accountId);

        EmitterType emitterType = switch (permitContainer.getPermitType()) {
            case HSE -> EmitterType.HSE;
            case WASTE -> EmitterType.WASTE;
            default -> EmitterType.GHGE;
        };

        installationAccountUpdateService.updateAccountUponTransferBGranted(
            accountId,
            emitterType,
            permitContainer.getPermit().getEstimatedAnnualEmissions().getQuantity());
    }
}
