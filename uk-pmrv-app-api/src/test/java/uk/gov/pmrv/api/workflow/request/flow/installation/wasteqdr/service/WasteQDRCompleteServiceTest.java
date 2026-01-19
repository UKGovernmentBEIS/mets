package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecision;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecisionType.ACCEPTED;

@ExtendWith(MockitoExtension.class)
public class WasteQDRCompleteServiceTest {

    @InjectMocks
    private WasteQDRCompleteService completeService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    public void addRequestAction() {
        final String requestId = "WQDR00001-2025-Q3";
        final Long accountId = 1L;

        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();

        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory("sign").build();
        final NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.WASTE_QDR_REGULATOR_REVIEW_SUBMIT_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();
        WasteQDRReviewAcceptedDecisionDetails decisionDetails = WasteQDRReviewAcceptedDecisionDetails
                .builder()
                .notes("notes")
                .build();
        WasteQDRReviewDecision reviewDecision = WasteQDRReviewDecision.builder()
                .type(ACCEPTED)
                .details(decisionDetails)
                .build();
        WasteQDRRequestPayload wasteQDRRequestPayload = WasteQDRRequestPayload.builder()
                .decisionNotification(decisionNotification)
                .reviewDecision(reviewDecision)
                .build();

        Request request = Request
                .builder()
                .payload(wasteQDRRequestPayload)
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.WASTE_QDR)
                .build();

        WasteQDRApplicationCompletedRequestActionPayload actionPayload = WasteQDRApplicationCompletedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.WASTE_QDR_APPLICATION_COMPLETED_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .reviewDecision(reviewDecision)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);

        completeService.addRequestAction(requestId);

        ArgumentCaptor<WasteQDRApplicationCompletedRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(WasteQDRApplicationCompletedRequestActionPayload.class);

        verify(requestService).addActionToRequest(eq(request),
                payloadCaptor.capture(),
                eq(RequestActionType.WASTE_QDR_APPLICATION_COMPLETED),
                eq(wasteQDRRequestPayload.getRegulatorReviewer()));

        WasteQDRApplicationCompletedRequestActionPayload captured = payloadCaptor.getValue();

        assertEquals(reviewDecision, captured.getReviewDecision());
    }
}
