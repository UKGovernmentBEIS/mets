package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;

import java.util.Set;

/**
 * Handler in permit issuance workflow for initializing installation operator details from installation account.
 */
@Service
@RequiredArgsConstructor
public class PermitIssuanceApplicationSubmitInitializer implements InitializeRequestTaskHandler {

	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails operatorDetailsSection = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

        return PermitIssuanceApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(operatorDetailsSection)
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT);
    }
}
