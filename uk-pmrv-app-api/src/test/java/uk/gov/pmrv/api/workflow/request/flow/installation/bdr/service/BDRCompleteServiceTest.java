package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRCompleteServiceTest {

    @InjectMocks
    private BDRCompleteService bdrCompleteService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @Test
    public void addRequestAction() {
        final String requestId = "BDR00001-2025";
        final Long accountId = 1L;
        UUID attachmentId = UUID.randomUUID();
        UUID attachmentId1 = UUID.randomUUID();

        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();

        BDRRequestPayload requestPayload = BDRRequestPayload
                .builder()
                .bdrAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewer("test")
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .build();

        Request request = Request
                .builder()
                .payload(requestPayload)
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.BDR)
                .build();

        BDRApplicationCompletedRequestActionPayload actionPayload = BDRApplicationCompletedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.BDR_APPLICATION_COMPLETED_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .bdrAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewAttachments(Map.of(attachmentId1,"test"))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);

        bdrCompleteService.addRequestAction(requestId);

        verify(requestService, times(1)).addActionToRequest(request,actionPayload, RequestActionType.BDR_APPLICATION_COMPLETED, requestPayload.getRegulatorReviewer());

    }
}
