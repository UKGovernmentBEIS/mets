package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceApplicationSubmitInitializer implements InitializeRequestTaskHandler {
    
    private final RequestRepository requestRepository;
    
    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final Long accountId = request.getAccountId();
        final List<RequestType> excludedTypes = 
            List.of(RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestType.NON_COMPLIANCE, RequestType.AVIATION_NON_COMPLIANCE);
        final List<RequestInfoDTO> requestInfoDTOS = requestRepository.findAllByAccountIdAndTypeNotIn(accountId, excludedTypes);
        return NonComplianceApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_APPLICATION_SUBMIT_PAYLOAD)
            .availableRequests(requestInfoDTOS)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NON_COMPLIANCE_APPLICATION_SUBMIT,
            RequestTaskType.AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT);
    }
}
