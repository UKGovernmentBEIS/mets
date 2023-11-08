package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationDreCancelAerService {

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;

    public void process(String requestId) {
        Request request = requestService.findRequestById(requestId);

        AviationDreRequestMetadata requestMetadata = (AviationDreRequestMetadata) request.getMetadata();
        Year reportingYear = requestMetadata.getYear();
        List<Request> openAerRequests =
            requestRepository.findByAccountIdAndTypeInAndStatus(request.getAccountId(), List.of(RequestType.AVIATION_AER_UKETS), RequestStatus.IN_PROGRESS);

        Optional<Request> openAerRequestForReportingYear = openAerRequests.stream()
            .filter(aer -> reportingYear.equals(((AviationAerRequestMetadata) aer.getMetadata()).getYear()))
            .findFirst();

        openAerRequestForReportingYear.ifPresent(this::cancelAer);
    }

    private void cancelAer(Request request) {
        //close workflow
        workflowService.deleteProcessInstance(request.getProcessInstanceId(), "AER workflow terminated because of DRE");

        //set request status to CANCELLED
        request.setStatus(RequestStatus.CANCELLED);

        //add corresponding timeline event
        requestService.addActionToRequest(
            request,
            null,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE,
            null
        );
    }
}
