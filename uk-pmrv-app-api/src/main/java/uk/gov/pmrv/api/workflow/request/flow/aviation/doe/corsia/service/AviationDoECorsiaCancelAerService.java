package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaCancelAerService {

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;

    public void process(String requestId) {
        Request request = requestService.findRequestById(requestId);

        AviationDoECorsiaRequestMetadata requestMetadata = (AviationDoECorsiaRequestMetadata) request.getMetadata();
        Year reportingYear = requestMetadata.getYear();
        List<Request> openAerRequests =
                requestRepository.findByAccountIdAndTypeInAndStatus(request.getAccountId(), List.of(RequestType.AVIATION_AER_CORSIA), RequestStatus.IN_PROGRESS);

        Optional<Request> openAerRequestForReportingYear = openAerRequests.stream()
                .filter(aer -> reportingYear.equals(((AviationAerCorsiaRequestMetadata) aer.getMetadata()).getYear()))
                .findFirst();

        openAerRequestForReportingYear.ifPresent(this::cancelAer);
    }

    private void cancelAer(Request request) {
        //close workflow
        workflowService.deleteProcessInstance(request.getProcessInstanceId(), "AER workflow terminated because of DOE");

        //set request status to CANCELLED
        request.setStatus(RequestStatus.CANCELLED);

        //add corresponding timeline event
        requestService.addActionToRequest(
                request,
                null,
                RequestActionType.AVIATION_AER_CORSIA_APPLICATION_CANCELLED_DUE_TO_DOE,
                null
        );
    }
}
