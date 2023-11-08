package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
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

@Service
@RequiredArgsConstructor
public class AviationAerReportingObligationService {

    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;
    private final RequestService requestService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;
    private final StartProcessRequestService startProcessRequestService;

    public void markAsExempt(Long accountId, String submitterId) {
        Year reportingYear = Year.now().minusYears(1);
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());

        List<Request> aviationAerRequests = requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());

        aviationAerRequests.forEach(request -> {
            if (RequestStatus.IN_PROGRESS.equals(request.getStatus())) {
                markAsExempt(request, submitterId);
            } else {
                //mark emissions for aer request as exempted
                ((AviationAerRequestMetadata) request.getMetadata()).setExempted(true);
            }

            //mark emissions derived from dre requests as exempted
            updateDreEmissionsExemptedFlag(accountId, reportingYear, true);
        });

        //reportable emissions for the reporting year here marked as exempted
        aviationReportableEmissionsService.updateReportableEmissionsExemptedFlag(accountId, reportingYear, true);
    }

    public void markAsExempt(Request request, String submitterId) {
        //terminate workflow
        workflowService.deleteProcessInstance(request.getProcessInstanceId(), "AER workflow terminated since related account was marked as exempt");

        //update request status to EXEMPT
        requestService.updateRequestStatus(request.getId(), RequestStatus.EXEMPT);

        //add cancelled timeline event
        requestService.addActionToRequest(request, null, RequestActionType.AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT, submitterId);

        //mark emissions for aer request as exempted
        ((AviationAerRequestMetadata) request.getMetadata()).setExempted(true);
    }

    public void revertExemption(Long accountId, String submitterId) {
        Year reportingYear = Year.now().minusYears(1);
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());
        List<Request> aviationAerRequests = requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());

        aviationAerRequests.forEach(request -> {
            if(RequestStatus.EXEMPT.equals(request.getStatus())) {
                revertExemptRequest(request, submitterId);
            } else {
                revertNonExemptRequest(request);
            }
        });
    }

    private void revertExemptRequest(Request request, String submitterId) {
        //start workflow again
        startProcessRequestService.reStartProcess(request);

        //add timeline event
        requestService.addActionToRequest(request, null, RequestActionType.AVIATION_AER_APPLICATION_RE_INITIATED, submitterId);

        //remove emissions from request metadata and set exempt flag to false
        AviationAerRequestMetadata requestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        requestMetadata.setEmissions(null);
        requestMetadata.setExempted(false);

        //mark emissions derived from dre requests as not exempted
        updateDreEmissionsExemptedFlag(request.getAccountId(), requestMetadata.getYear(), false);

        //remove reportable emissions
        aviationReportableEmissionsService.deleteReportableEmissions(request.getAccountId(), requestMetadata.getYear());
    }

    /**
     * Perform actions needed when the reporting status of the account linked to the provided request turns to
     * {@link AviationAccountReportingStatus#REQUIRED_TO_REPORT} and the request has already been finalized,
     * i.e. request status is {@link RequestStatus#COMPLETED} or {@link RequestStatus#CANCELLED}.
     * @param request {@link Request}
     */
    private void revertNonExemptRequest(Request request) {
        //mark emissions for aer request as not exempted
        AviationAerRequestMetadata requestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        requestMetadata.setExempted(false);

        //mark emissions derived from dre requests as not exempted
        updateDreEmissionsExemptedFlag(request.getAccountId(), requestMetadata.getYear(), false);

        //reportable emissions for the reporting year here marked as not exempted
        aviationReportableEmissionsService.updateReportableEmissionsExemptedFlag(request.getAccountId(), requestMetadata.getYear(), false);
    }

    private void updateDreEmissionsExemptedFlag(Long accountId, Year reportingYear, boolean exempted) {
        List<Request> aviationDreRequests = requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, List.of(RequestType.AVIATION_DRE_UKETS.name()), reportingYear.getValue());

        aviationDreRequests.forEach(request -> ((AviationDreRequestMetadata) request.getMetadata()).setExempted(exempted));
    }
}
