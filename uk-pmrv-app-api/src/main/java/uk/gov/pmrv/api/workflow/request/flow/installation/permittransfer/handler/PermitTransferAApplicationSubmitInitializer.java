package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;

@Service
@RequiredArgsConstructor
public class PermitTransferAApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        return PermitTransferAApplicationRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD)
            .permitTransferDetails(PermitTransferDetails.builder().build())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_TRANSFER_A_APPLICATION_SUBMIT);
    }
}
