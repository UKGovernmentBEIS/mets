package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreCancelAerServiceTest {

    @InjectMocks
    private AviationDreCancelAerService cancelAerService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process_open_aer_workflow_exists() {
        String dreRequestId = "REQ-1";
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        AviationDreRequestMetadata dreRequestMetadata = AviationDreRequestMetadata.builder().type(RequestMetadataType.AVIATION_DRE).year(reportingYear).build();
        Request dreRequest = Request.builder()
            .id(dreRequestId)
            .accountId(accountId)
            .metadata(dreRequestMetadata)
            .build();

        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().type(RequestMetadataType.AVIATION_AER).year(reportingYear).build();
        String aerProcessInstanceId = "processInstanceId";
        Request aerRequest = Request.builder().processInstanceId(aerProcessInstanceId).metadata(aerRequestMetadata).status(RequestStatus.IN_PROGRESS).build();

        when(requestService.findRequestById(dreRequestId)).thenReturn(dreRequest);
        when(requestRepository
            .findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.AVIATION_AER_UKETS), RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(aerRequest));


        cancelAerService.process(dreRequestId);

        //verify
        assertEquals(RequestStatus.CANCELLED, aerRequest.getStatus());

        verify(requestService, times(1)).findRequestById(dreRequestId);
        verify(requestRepository, times(1))
            .findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.AVIATION_AER_UKETS), RequestStatus.IN_PROGRESS);
        verify(workflowService, times(1)).deleteProcessInstance(eq(aerProcessInstanceId), anyString());
        verify(requestService, times(1))
            .addActionToRequest(aerRequest, null, RequestActionType.AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE, null);
    }

    @Test
    void process_open_aer_workflow_not_exists() {
        String dreRequestId = "REQ-1";
        Long accountId = 1L;
        Year reportingYear = Year.of(2022);
        AviationDreRequestMetadata dreRequestMetadata = AviationDreRequestMetadata.builder().type(RequestMetadataType.AVIATION_DRE).year(reportingYear).build();
        Request dreRequest = Request.builder()
            .id(dreRequestId)
            .accountId(accountId)
            .metadata(dreRequestMetadata)
            .build();

        AviationAerRequestMetadata aerRequestMetadata = AviationAerRequestMetadata.builder().type(RequestMetadataType.AVIATION_AER).year(Year.of(2021)).build();
        String aerProcessInstanceId = "processInstanceId";
        Request aerRequest = Request.builder().processInstanceId(aerProcessInstanceId).metadata(aerRequestMetadata).status(RequestStatus.IN_PROGRESS).build();

        when(requestService.findRequestById(dreRequestId)).thenReturn(dreRequest);
        when(requestRepository
            .findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.AVIATION_AER_UKETS), RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(aerRequest));


        cancelAerService.process(dreRequestId);

        //verify
        verify(requestService, times(1)).findRequestById(dreRequestId);
        verify(requestRepository, times(1))
            .findByAccountIdAndTypeInAndStatus(accountId, List.of(RequestType.AVIATION_AER_UKETS), RequestStatus.IN_PROGRESS);
        verifyNoInteractions(workflowService);
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
    }
}