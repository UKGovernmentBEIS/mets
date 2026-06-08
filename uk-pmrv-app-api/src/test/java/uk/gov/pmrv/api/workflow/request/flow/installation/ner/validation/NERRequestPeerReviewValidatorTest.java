package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERRequestPeerReviewValidatorTest {

    @InjectMocks
    private NERRequestPeerReviewValidator validator;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Mock
    private NERValidationService nerValidationService;

    @Test
    void validate() {
        String peerReviewer = "peerReviewer";

        AppUser appUser = AppUser.builder()
                .userId("user")
                .build();

        PeerReviewRequestTaskActionPayload actionPayload =
                PeerReviewRequestTaskActionPayload.builder()
                        .peerReviewer(peerReviewer)
                        .build();

        NER ner = NER.builder().build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .ner(ner)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();

        validator.validate(requestTask, actionPayload, appUser);

        verify(peerReviewerTaskAssignmentValidator).validate(
                RequestTaskType.NER_APPLICATION_PEER_REVIEW,
                peerReviewer,
                appUser
        );

        verify(nerValidationService).validateNer(ner);
    }
}
