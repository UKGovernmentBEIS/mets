package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationCompletedRequestActionPayload;

import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BDRS2CompleteServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @InjectMocks
    private BDRS2CompleteService service;


    @Test
    void addRequestAction_shouldAddCompletedActionToRequest() {
        // given
        String requestId = "REQ-1";
        Long accountId = 1L;
        Long verificationBodyId = 2L;

        Request request = mock(Request.class);
        BDRS2RequestPayload requestPayload = mock(BDRS2RequestPayload.class);
        BDRS2VerificationReport verificationReport = mock(BDRS2VerificationReport.class);
        InstallationOperatorDetails operatorDetails = mock(InstallationOperatorDetails.class);

        Map<UUID,String> bdrs2Attachments = new HashMap<>();
        Map<UUID,String> regulatorAttachments =  new HashMap<>();
        String regulatorReviewer = "reviewer";

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(request.getAccountId()).thenReturn(accountId);
        when(request.getVerificationBodyId()).thenReturn(verificationBodyId);

        when(requestPayload.getVerificationReport()).thenReturn(verificationReport);
        when(requestPayload.getBdrs2Attachments()).thenReturn(bdrs2Attachments);
        when(requestPayload.getRegulatorReviewAttachments()).thenReturn(regulatorAttachments);
        when(requestPayload.getRegulatorReviewer()).thenReturn(regulatorReviewer);

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
                .thenReturn(operatorDetails);

        ArgumentCaptor<BDRS2ApplicationCompletedRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(BDRS2ApplicationCompletedRequestActionPayload.class);

        // when
        service.addRequestAction(requestId);

        // then
        verify(requestVerificationService).refreshVerificationReportVBDetails(
                verificationReport,
                verificationBodyId
        );

        verify(requestService).addActionToRequest(
                eq(request),
                payloadCaptor.capture(),
                eq(RequestActionType.BDRS2_APPLICATION_COMPLETED),
                eq(regulatorReviewer)
        );

        BDRS2ApplicationCompletedRequestActionPayload captured = payloadCaptor.getValue();

        assertEquals(bdrs2Attachments, captured.getBdrs2Attachments());
        assertEquals(regulatorAttachments, captured.getRegulatorReviewAttachments());
    }
}
