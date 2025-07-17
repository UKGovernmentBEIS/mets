package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation.PermitVariationReviewRequestPeerReviewRegulatorLedValidator;

@ExtendWith(MockitoExtension.class)
public class PermitVariationReviewRequestPeerReviewRegulatorLedValidatorTest {

	@InjectMocks
    private PermitVariationReviewRequestPeerReviewRegulatorLedValidator cut;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    @Mock
    private PermitGrantedValidatorService permitGrantedValidatorService;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void validate() {
    	LocalDate activationDate = LocalDate.now();
        String selectedPeerReviewer = "selectedPeerReviewer";
        AppUser appUser = AppUser.builder().userId("userId").build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        PermitVariationRegulatorLedGrantDetermination determination = PermitVariationRegulatorLedGrantDetermination.builder()
            .activationDate(activationDate)
            .reason("determination reason")
            .build();
        PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD)
            .permit(Permit.builder()
            		.abbreviations(Abbreviations.builder().exist(false).build())
            		.build())
            .determination(determination)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .type(RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW)
            .payload(requestTaskPayload)
            .build();

        cut.validate(requestTask, taskActionPayload, appUser);

        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(peerReviewerTaskAssignmentValidator, times(1))
            .validate(RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW, selectedPeerReviewer, appUser);
        verify(permitReviewDeterminationValidatorService, times(1)).validateDeterminationObject(determination);
        verify(permitGrantedValidatorService, times(1)).validatePermit(PermitContainer.builder()
        		.activationDate(activationDate)
        		.permit(
	        		Permit.builder()
	        		.abbreviations(Abbreviations.builder().exist(false).build())
	        		.build())
        		.build());
    }
}
