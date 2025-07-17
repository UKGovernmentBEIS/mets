package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitTransferBApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final PermitTransferMapper PERMIT_TRANSFER_MAPPER = Mappers.getMapper(PermitTransferMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        
        final InstallationOperatorDetails installationOperatorDetails = 
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());

        final PermitTransferBRequestPayload requestPayload = (PermitTransferBRequestPayload) request.getPayload();

        return PERMIT_TRANSFER_MAPPER.toPermitTransferBApplicationAmendsSubmitRequestTaskPayload(requestPayload, installationOperatorDetails);
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT);
    }
}
