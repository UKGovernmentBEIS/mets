package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerReportingObligationServiceTest {

    @InjectMocks
    private AviationAerReportingObligationService aerReportingObligationService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void markAsExempted_when_request_status_in_progress() {
        String aerRequestId = "req-id";
        String submitterId = "submitterId";
        String processInstanceId = "processInstanceId";
        Year reportingYear = Year.of(2022);
        Long accountId = 1L;
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();
        Request aerRequest = Request.builder()
            .id(aerRequestId)
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .status(RequestStatus.IN_PROGRESS)
            .processInstanceId(processInstanceId)
            .metadata(aerRequestMetadata)
            .build();

        List<String> aviationDreRequestTypes = List.of(RequestType.AVIATION_DRE_UKETS.name());
        AviationDreRequestMetadata dreRequestMetadata = AviationDreRequestMetadata.builder().year(reportingYear).build();
        Request dreRequest = Request.builder()
            .accountId(accountId)
            .type(RequestType.AVIATION_DRE_UKETS)
            .metadata(dreRequestMetadata)
            .build();

        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(aerRequest));
        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(dreRequest));

        assertFalse(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertFalse(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        //invoke
        aerReportingObligationService.markAsExempt(accountId, submitterId);

        //verify

        assertTrue(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertTrue(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());
        verify(workflowService, times(1)).deleteProcessInstance(eq(processInstanceId), anyString());
        verify(requestService, times(1)).updateRequestStatus(aerRequestId, RequestStatus.EXEMPT);
        verify(requestService, times(1))
            .addActionToRequest(aerRequest, null, RequestActionType.AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT, submitterId );
        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue());
        verify(aviationReportableEmissionsService, times(1))
            .updateReportableEmissionsExemptedFlag(accountId, reportingYear, true);
    }

    @Test
    void markAsExempted_when_request_status_completed() {
        String aerRequestId = "req-id";
        String submitterId = "submitterId";
        String processInstanceId = "processInstanceId";
        Year reportingYear = Year.of(2022);
        Long accountId = 1L;
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();
        Request aerRequest = Request.builder()
            .id(aerRequestId)
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .status(RequestStatus.COMPLETED)
            .processInstanceId(processInstanceId)
            .metadata(aerRequestMetadata)
            .build();

        List<String> aviationDreRequestTypes = List.of(RequestType.AVIATION_DRE_UKETS.name());
        AviationDreRequestMetadata dreRequestMetadata = AviationDreRequestMetadata.builder().year(reportingYear).build();
        Request dreRequest = Request.builder()
            .accountId(accountId)
            .type(RequestType.AVIATION_DRE_UKETS)
            .metadata(dreRequestMetadata)
            .build();

        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(aerRequest));
        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(dreRequest));

        assertFalse(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertFalse(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        //invoke
        aerReportingObligationService.markAsExempt(accountId, submitterId);

        //verify

        assertTrue(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertTrue(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());
        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue());
        verify(aviationReportableEmissionsService, times(1))
            .updateReportableEmissionsExemptedFlag(accountId, reportingYear, true);
        verifyNoInteractions(workflowService, requestService);
    }

    @Test
    void markAsExempted_when_request_not_found() {
        String submitterId = "submitterId";
        Year reportingYear = Year.of(2022);
        Long accountId = 1L;
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());

        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of());

        //invoke
        aerReportingObligationService.markAsExempt(accountId, submitterId);

        //verify
        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());
        verify(aviationReportableEmissionsService, times(1))
            .updateReportableEmissionsExemptedFlag(accountId, reportingYear, true);
        verifyNoMoreInteractions(requestRepository);
        verifyNoInteractions(workflowService, requestService);
    }

    @Test
    void markAsExempted_request_input() {
        String requestId = "req-id";
        String processInstanceId = "processInstanceId";
        Year reportingYear = Year.of(2022);
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).build();
        Request request = Request.builder().id(requestId).processInstanceId(processInstanceId).metadata(aerRequestMetadata).build();
        String submitterId = "submitterId";

        assertFalse(((AviationAerRequestMetadata)request.getMetadata()).isExempted());

        aerReportingObligationService.markAsExempt(request, submitterId);

        //verify
        assertTrue(((AviationAerRequestMetadata)request.getMetadata()).isExempted());

        verify(workflowService, times(1)).deleteProcessInstance(eq(processInstanceId), anyString());
        verify(requestService, times(1)).updateRequestStatus(requestId, RequestStatus.EXEMPT);
        verify(requestService, times(1)).addActionToRequest(request, null, RequestActionType.AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT, submitterId );
    }

    @Test
    void revertExemption_when_request_status_exempted() {
        String aerRequestId = "req-id";
        String submitterId = "submitterId";
        Year reportingYear = Year.of(2022);
        Long accountId = 1L;
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder()
            .year(reportingYear)
            .emissions(BigDecimal.TEN)
            .isExempted(true)
            .build();
        Request aerRequest = Request.builder()
            .id(aerRequestId)
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .status(RequestStatus.EXEMPT)
            .metadata(aerRequestMetadata)
            .build();

        List<String> aviationDreRequestTypes = List.of(RequestType.AVIATION_DRE_UKETS.name());
        AviationDreRequestMetadata dreRequestMetadata = AviationDreRequestMetadata.builder().year(reportingYear).isExempted(true).build();
        Request dreRequest = Request.builder()
            .accountId(accountId)
            .type(RequestType.AVIATION_DRE_UKETS)
            .metadata(dreRequestMetadata)
            .build();


        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(aerRequest));
        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(dreRequest));

        assertTrue(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertTrue(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        //invoke
        aerReportingObligationService.revertExemption(accountId, submitterId);

        //verify

        assertFalse(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        Assertions.assertNull(((AviationAerRequestMetadata)aerRequest.getMetadata()).getEmissions());
        assertFalse(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());
        verify(startProcessRequestService, times(1)).reStartProcess(aerRequest);
        verify(requestService, times(1))
            .addActionToRequest(aerRequest, null, RequestActionType.AVIATION_AER_APPLICATION_RE_INITIATED, submitterId );
        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue());
        verify(aviationReportableEmissionsService, times(1)).deleteReportableEmissions(accountId, reportingYear);

        verify(requestService, never()).updateRequestStatus(any(), any());
        verifyNoInteractions(workflowService);
    }

    @Test
    void revertExemption_when_request_status_completed() {
        String aerRequestId = "req-id";
        String submitterId = "submitterId";
        Year reportingYear = Year.of(2022);
        Long accountId = 1L;
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());
        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().year(reportingYear).isExempted(true).build();
        Request aerRequest = Request.builder()
            .id(aerRequestId)
            .type(RequestType.AVIATION_AER_UKETS)
            .accountId(accountId)
            .status(RequestStatus.COMPLETED)
            .metadata(aerRequestMetadata)
            .build();

        List<String> aviationDreRequestTypes = List.of(RequestType.AVIATION_DRE_UKETS.name());
        AviationDreRequestMetadata dreRequestMetadata = AviationDreRequestMetadata.builder().year(reportingYear).isExempted(true).build();
        Request dreRequest = Request.builder()
            .accountId(accountId)
            .type(RequestType.AVIATION_DRE_UKETS)
            .metadata(dreRequestMetadata)
            .build();

        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(aerRequest));
        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of(dreRequest));

        assertTrue(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertTrue(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        //invoke
        aerReportingObligationService.revertExemption(accountId, submitterId);

        //verify

        assertFalse(((AviationAerRequestMetadata)aerRequest.getMetadata()).isExempted());
        assertFalse(((AviationDreRequestMetadata)dreRequest.getMetadata()).isExempted());

        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());
        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationDreRequestTypes, reportingYear.getValue());
        verify(aviationReportableEmissionsService, times(1))
            .updateReportableEmissionsExemptedFlag(accountId, reportingYear, false);

        verifyNoInteractions(workflowService, startProcessRequestService, requestService);
    }

    @Test
    void revertExemption_when_no_request_found() {
        String submitterId = "submitterId";
        Year reportingYear = Year.of(2022);
        Long accountId = 1L;
        List<String> aviationAerRequestTypes = List.of(RequestType.AVIATION_AER_UKETS.name(), RequestType.AVIATION_AER_CORSIA.name());

        when(requestRepository
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue()))
            .thenReturn(List.of());

        //invoke
        aerReportingObligationService.revertExemption(accountId, submitterId);

        //verify

        verify(requestRepository, times(1))
            .findAllByAccountIdAndTypeInAndMetadataYear(accountId, aviationAerRequestTypes, reportingYear.getValue());

        verifyNoMoreInteractions(requestRepository);
        verifyNoInteractions(workflowService, startProcessRequestService, requestService, aviationReportableEmissionsService);
    }
}