package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import java.util.Set;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper.PermitTransferMapper;

@Service
@RequiredArgsConstructor
public class PermitTransferBWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final PermitTransferMapper PERMIT_TRANSFER_MAPPER = Mappers.getMapper(PermitTransferMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());

        return PERMIT_TRANSFER_MAPPER.toPermitTransferBApplicationReviewRequestTaskPayload(
            (PermitTransferBRequestPayload) request.getPayload(),
            installationOperatorDetails,
            RequestTaskPayloadType.PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW);
    }
}
