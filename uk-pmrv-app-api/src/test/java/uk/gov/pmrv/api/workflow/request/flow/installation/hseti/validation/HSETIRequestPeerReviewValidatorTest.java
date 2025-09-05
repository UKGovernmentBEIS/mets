package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HSETIRequestPeerReviewValidatorTest {

    @InjectMocks
    private HSETIRequestPeerReviewValidator validator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private HSETIValidatorService hsetiValidatorService;

    @Test
    void validate_shouldValidateSuccessfully() {
        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";

        UUID hsetiFile = UUID.randomUUID();
        final HSETI hseti = HSETI.builder().hsetiFile(hsetiFile).files(Set.of(hsetiFile)).notes("test").build();

        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .payloadType(RequestTaskActionPayloadType.HSE_TI_REQUEST_PEER_REVIEW_PAYLOAD)
                .build();
        final Request request = Request.builder().id("2").build();
        final RequestTaskPayload requestTaskPayload = HSETIApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.HSE_TI_APPLICATION_PEER_REVIEW_PAYLOAD)
                .hseti(hseti)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .processTaskId("processTaskId")
                .payload(requestTaskPayload)
                .build();

        validator.validate(requestTask, taskActionPayload, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(
                RequestTaskType.HSE_TI_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);
        verify(hsetiValidatorService, times(1)).validateHSETI(hseti);
    }
}
