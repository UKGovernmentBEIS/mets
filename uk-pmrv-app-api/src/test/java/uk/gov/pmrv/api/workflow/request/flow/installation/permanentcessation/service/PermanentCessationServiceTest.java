package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationSaveApplicationRequestTaskActionPayload;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationServiceTest {

    @InjectMocks
    private PermanentCessationService permanentCessationService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTask requestTask;

    @Mock
    private PermanentCessationApplicationSubmitRequestTaskPayload taskPayload;

    @Test
    void cancel() {
        final String requestId = "1";
        final String regulatorAssignee = "regulator";

        final Request request = Request.builder()
                .id(requestId)
                .payload(InstallationOnsiteInspectionRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        permanentCessationService.cancel(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, RequestActionType.PERMANENT_CESSATION_APPLICATION_CANCELLED, regulatorAssignee);
    }

    @Test
    void testApplySavePayload() {

        PermanentCessationSaveApplicationRequestTaskActionPayload actionPayload = mock(PermanentCessationSaveApplicationRequestTaskActionPayload.class);
        when(actionPayload.getPermanentCessation()).thenReturn(mock(PermanentCessation.class));
        when(actionPayload.getPermanentCessationAttachments()).thenReturn(Map.of());
        when(actionPayload.getPermanentCessationSectionsCompleted()).thenReturn(Map.of("TEST", true, "OTHER", false));

        when(requestTask.getPayload()).thenReturn(taskPayload);
        permanentCessationService.applySavePayload(actionPayload, requestTask);

        verify(taskPayload).setPermanentCessation(actionPayload.getPermanentCessation());
        verify(taskPayload).setPermanentCessationSectionsCompleted(actionPayload.getPermanentCessationSectionsCompleted());
        verify(taskPayload).setPermanentCessationAttachments(actionPayload.getPermanentCessationAttachments());
    }

    @Test
    void requestPeerReview() {
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();
        final String selectedPeerReviewer = "selectedPeerReviewer";

        final Map<String, Boolean> sectionsCompleted = Map.of("subtask", true);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "test.png");

        PermanentCessation permanentCessation = PermanentCessation.builder().build();

        Request request = Request.builder()
                .payload(PermanentCessationRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMANENT_CESSATION_REQUEST_PAYLOAD)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(PermanentCessationApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_PAYLOAD)
                        .permanentCessation(permanentCessation)
                        .permanentCessationSectionsCompleted(sectionsCompleted)
                        .permanentCessationAttachments(attachments)
                        .build())
                .build();

        final PermanentCessationRequestPayload expectedPayload = PermanentCessationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMANENT_CESSATION_REQUEST_PAYLOAD)
                .permanentCessation(permanentCessation)
                .permanentCessationSectionsCompleted(sectionsCompleted)
                .permanentCessationAttachments(attachments)
                .regulatorPeerReviewer(selectedPeerReviewer)
                .regulatorReviewer(user.getUserId())
                .build();

        permanentCessationService.requestPeerReview(requestTask, selectedPeerReviewer, user);

        PermanentCessationRequestPayload updatedPayload = (PermanentCessationRequestPayload) requestTask.getRequest().getPayload();
        Assertions.assertEquals(expectedPayload, updatedPayload);
    }
}
