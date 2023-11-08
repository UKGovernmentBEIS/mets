package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper.InstallationAccountPayloadMapper;

import java.util.Set;

@Service
public class InstallationAccountOpeningApplicationReviewInitializer implements InitializeRequestTaskHandler {

    private static final InstallationAccountPayloadMapper INSTALLATION_ACCOUNT_PAYLOAD_MAPPER = Mappers.getMapper(InstallationAccountPayloadMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationAccountOpeningRequestPayload requestPayload = (InstallationAccountOpeningRequestPayload) request.getPayload();
        return INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toInstallationAccountOpeningApplicationRequestTaskPayload(requestPayload.getAccountPayload());
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
    }
}
