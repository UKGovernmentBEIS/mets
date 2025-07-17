package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.util.Set;

@Service
public class DoalAuthorityResponseInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        return DoalAuthorityResponseRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_AUTHORITY_RESPONSE_PAYLOAD)
                .regulatorPreliminaryAllocations(requestPayload.getDoal().getActivityLevelChangeInformation()
                        .getPreliminaryAllocations())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.DOAL_AUTHORITY_RESPONSE);
    }
}
