package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationRegulatorLedServiceTest {

    @InjectMocks
    private PermitVariationRegulatorLedService cut;

    @Mock
    private PermitReviewService permitReviewService;

    @Mock
    private PermitValidatorService permitValidatorService;

    @Test
    void saveReviewGroupDecisionRegulatorLed() {
    	PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload taskActionPayload = PermitVariationSaveReviewGroupDecisionRegulatorLedRequestTaskActionPayload.builder()
                .decision(PermitAcceptedVariationDecisionDetails.builder().variationScheduleItems(List.of("A change required", "A second change required")).build())
                .group(PermitReviewGroup.CONFIDENTIALITY_STATEMENT)
                .build();

    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder().build();
            RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
                .build();

        cut.saveReviewGroupDecisionRegulatorLed(taskActionPayload, requestTask);
            
        assertThat(taskPayload.getReviewGroupDecisions()).containsEntry(taskActionPayload.getGroup(), taskActionPayload.getDecision());	
    }

    @Test
    void saveDetailsReviewGroupDecisionRegulatorLed() {
    	PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload taskActionPayload = PermitVariationSaveDetailsReviewGroupDecisionRegulatorLedRequestTaskActionPayload.builder()
                .decision(PermitAcceptedVariationDecisionDetails.builder().variationScheduleItems(List.of("A change required", "A second change required")).build())
                .build();

    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder().build();
            RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
                .build();

        cut.saveDetailsReviewGroupDecisionRegulatorLed(taskActionPayload, requestTask);
            
        assertThat(taskPayload.getPermitVariationDetailsReviewDecision()).isEqualTo(taskActionPayload.getDecision());	
    }

    @Test
    void saveDeterminationRegulatorLed() {
    	PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload actionPayload = PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload.builder()
    			.determination(PermitVariationRegulatorLedGrantDetermination.builder()
    					.activationDate(LocalDate.of(LocalDate.now().getYear() + 1, 1, 1))
    					.reason("reason")
    					.logChanges("logChanges")
    					.reasonTemplate(PermitVariationReasonTemplate.FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR)
    					.build())
    			.reviewSectionsCompleted(Map.of("determinatino", false))
    			.build();
    	
    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
    			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.payload(taskPayload)
    			.build();
    	
    	cut.saveDeterminationRegulatorLed(actionPayload, requestTask);
    	
    	assertThat(taskPayload.getDetermination()).isEqualTo(actionPayload.getDetermination());
    	assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(actionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void saveRequestPeerReviewActionRegulatorLed() {
    	final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
    	final String selectedPeerReviewer = "peerReviewer";
    	
    	final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder().build();
        final Request request = Request.builder()
            .payload(requestPayload)
            .build();
        
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload =
        		PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
                .permitType(PermitType.GHGE)
                .permit(Permit.builder()
                    .abbreviations(Abbreviations.builder().exist(true).build())
                    .build())
                .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
                .permitSectionsCompleted(Map.of("sec1", List.of(true)))
                .permitVariationDetailsCompleted(true)
                .reviewSectionsCompleted(Map.of("reviewSec1", true))
                .reviewGroupDecisions(
                    Map.of(PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
                            .notes("notes").build()))
                .permitVariationDetailsReviewDecision(PermitAcceptedVariationDecisionDetails.builder()
                        .notes("notes").build())
                .determination(PermitVariationRegulatorLedGrantDetermination.builder()
                    .reason("reason")
                    .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(requestTaskPayload)
            .build();
        
        cut.saveRequestPeerReviewActionRegulatorLed(requestTask, selectedPeerReviewer, pmrvUser.getUserId());
        
        assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(selectedPeerReviewer);
    	assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(pmrvUser.getUserId());
    	assertThat(requestPayload.getPermitType()).isEqualTo(requestTaskPayload.getPermitType());
    	assertThat(requestPayload.getPermit()).isEqualTo(requestTaskPayload.getPermit());
    	assertThat(requestPayload.getPermitVariationDetails()).isEqualTo(requestTaskPayload.getPermitVariationDetails());
    	assertThat(requestPayload.getPermitSectionsCompleted()).isEqualTo(requestTaskPayload.getPermitSectionsCompleted());
    	assertThat(requestPayload.getPermitVariationDetailsCompleted()).isEqualTo(requestTaskPayload.getPermitVariationDetailsCompleted());
    	assertThat(requestPayload.getReviewSectionsCompleted()).isEqualTo(requestTaskPayload.getReviewSectionsCompleted());
    	assertThat(requestPayload.getReviewGroupDecisionsRegulatorLed()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
    	assertThat(requestPayload.getPermitVariationDetailsReviewDecisionRegulatorLed()).isEqualTo(requestTaskPayload.getPermitVariationDetailsReviewDecision());
    	assertThat(requestPayload.getDeterminationRegulatorLed()).isEqualTo(requestTaskPayload.getDetermination());
    }

    @Test
    void savePermitVariationDecisionNotificationRegulatorLed() {
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.build();
    	Request request = Request.builder().payload(requestPayload).build();
    	
    	UUID attachment1 = UUID.randomUUID();
    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
    			.permitSectionsCompleted(Map.of("section1", List.of(true)))
    			.permitVariationDetailsCompleted(true)
    			.permitAttachments(Map.of(attachment1, "att1"))
    			.reviewSectionsCompleted(Map.of("reviewSection1", true))
    			.reviewGroupDecisions(Map.of(PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder().notes("notes").build()))
    			.permitVariationDetailsReviewDecision(PermitAcceptedVariationDecisionDetails.builder().notes("notes2").build())
    			.determination(PermitVariationRegulatorLedGrantDetermination.builder().logChanges("logChanges").build())
    			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();
    	DecisionNotification decisionNotification = DecisionNotification.builder()
    			.signatory("sign")
    			.operators(Set.of("oper"))
    			.build();
    	PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
    	
    	cut.savePermitVariationDecisionNotificationRegulatorLed(requestTask, decisionNotification, pmrvUser);
    	
    	assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
    	assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(pmrvUser.getUserId());
    	assertThat(requestPayload.getReviewSectionsCompleted()).isEqualTo(requestTaskPayload.getReviewSectionsCompleted());
    	assertThat(requestPayload.getReviewGroupDecisionsRegulatorLed()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
    	assertThat(requestPayload.getPermitVariationDetailsReviewDecisionRegulatorLed()).isEqualTo(requestTaskPayload.getPermitVariationDetailsReviewDecision());
    	assertThat(requestPayload.getDeterminationRegulatorLed()).isEqualTo(requestTaskPayload.getDetermination());
    	assertThat(requestPayload.getPermitType()).isEqualTo(requestTaskPayload.getPermitType());
    	assertThat(requestPayload.getPermitVariationDetails()).isEqualTo(requestTaskPayload.getPermitVariationDetails());
    	assertThat(requestPayload.getPermitSectionsCompleted()).isEqualTo(requestTaskPayload.getPermitSectionsCompleted());
    	assertThat(requestPayload.getPermitVariationDetailsCompleted()).isEqualTo(requestTaskPayload.getPermitVariationDetailsCompleted());
    	assertThat(requestPayload.getPermitAttachments()).isEqualTo(requestTaskPayload.getPermitAttachments());
    }
}

