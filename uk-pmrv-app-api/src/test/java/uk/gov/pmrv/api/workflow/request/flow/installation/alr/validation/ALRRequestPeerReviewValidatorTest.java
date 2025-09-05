package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRRequestPeerReviewValidatorTest {

    @InjectMocks
    private ALRRequestPeerReviewValidator validator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private ALRValidationService alrValidationService;

    @Test
    void validate_shouldValidateSuccessfully() {
        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final ALR alr = new ALR();
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .payloadType(RequestTaskActionPayloadType.BDR_REQUEST_PEER_REVIEW_PAYLOAD)
                .build();
        final Request request = Request.builder().id("2").build();
        final RequestTaskPayload requestTaskPayload = ALRApplicationRegulatorReviewSubmitRequestTaskPayload.builder().payloadType(RequestTaskPayloadType.ALR_APPLICATION_PEER_REVIEW_PAYLOAD)
                .alr(alr)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .processTaskId("processTaskId")
                .payload(requestTaskPayload)
                .build();

        validator.validate(requestTask, taskActionPayload, appUser);

        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(
                RequestTaskType.ALR_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);
        verify(alrValidationService, times(1)).validateALR(alr);
    }
}
