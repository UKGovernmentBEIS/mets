package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewServiceTest {

    @InjectMocks
    private PermitVariationReviewService cut;

    @Mock
    private PermitReviewService permitReviewService;

    @Mock
    private PermitValidatorService permitValidatorService;

    @Test
    void savePermitVariation() {
        PermitVariationSaveApplicationReviewRequestTaskActionPayload taskActionPayload = PermitVariationSaveApplicationReviewRequestTaskActionPayload.builder()
            .permitVariationDetails(PermitVariationDetails.builder()
                .reason("reason")
                .build())
            .permitVariationDetailsCompleted(Boolean.TRUE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(true).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                    .monitoringApproaches(Map.of(
                        MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().build()
                    ))
                    .build())
                .build())
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .reviewSectionsCompleted(Map.of("section2", true))
            .permitVariationDetailsReviewCompleted(Boolean.TRUE)
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .permit(Permit.builder().build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.savePermitVariation(taskActionPayload, requestTask);

        verify(permitReviewService, times(1)).cleanUpDeprecatedReviewGroupDecisions(taskPayload, Set.of(MonitoringApproachType.CALCULATION_CO2));
        verify(permitReviewService, times(1)).resetDeterminationIfNotDeemedWithdrawn(taskPayload);

        assertThat(taskPayload.getPermitVariationDetails()).isEqualTo(taskActionPayload.getPermitVariationDetails());
        assertThat(taskPayload.getPermitVariationDetailsCompleted()).isEqualTo(taskActionPayload.getPermitVariationDetailsCompleted());
        assertThat(taskPayload.getPermit()).isEqualTo(taskActionPayload.getPermit());
        assertThat(taskPayload.getPermitSectionsCompleted()).isEqualTo(taskActionPayload.getPermitSectionsCompleted());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getPermitVariationDetailsReviewCompleted()).isTrue();
    }

    @Test
    void saveReviewGroupDecision() {
        PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload = PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload.builder()
            .decision(
                PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).details(
                    PermitAcceptedVariationDecisionDetails.builder().variationScheduleItems(List.of("A change required", "A second change required")).build()).build()
            )
            .group(PermitReviewGroup.CONFIDENTIALITY_STATEMENT)
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.saveReviewGroupDecision(taskActionPayload, requestTask);

        verify(permitReviewService, times(1)).resetDeterminationIfNotValidWithDecisions(taskPayload, RequestType.PERMIT_VARIATION);

        assertThat(taskPayload.getPermitVariationDetailsReviewDecision()).isNull();
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getReviewGroupDecisions()).containsEntry(taskActionPayload.getGroup(), taskActionPayload.getDecision());
    }

    @Test
    void saveDetailsReviewGroupDecision() {
        PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload taskActionPayload = PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload.builder()
            .decision(
                PermitVariationReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).details(
                    PermitAcceptedVariationDecisionDetails.builder().variationScheduleItems(List.of("A change required", "A second change required")).notes("notes")
                        .build())
                    .build()
            )
            .reviewSectionsCompleted(Map.of("section", true))
            .permitVariationDetailsReviewCompleted(Boolean.TRUE)
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.saveDetailsReviewGroupDecision(taskActionPayload, requestTask);

        verify(permitReviewService, times(1)).resetDeterminationIfNotValidWithDecisions(taskPayload, RequestType.PERMIT_VARIATION);

        assertThat(taskPayload.getPermitVariationDetailsReviewDecision()).isEqualTo(taskActionPayload.getDecision());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getPermitVariationDetailsReviewCompleted()).isTrue();
    }

    @Test
    void saveDetermination() {
        PermitVariationSaveReviewDeterminationRequestTaskActionPayload taskActionPayload = PermitVariationSaveReviewDeterminationRequestTaskActionPayload.builder()
            .determination(
                PermitVariationGrantDetermination.builder()
                    .type(DeterminationType.GRANTED)
                    .logChanges("log changes")
                    .activationDate(LocalDate.now().plusDays(10))
                    .reason("reason")
                    .build())
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.saveDetermination(taskActionPayload, requestTask);

        assertThat(taskPayload.getDetermination()).isEqualTo(taskActionPayload.getDetermination());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void savePermitVariationDecisionNotification() {
    	UUID attachment1 = UUID.randomUUID();
    	UUID attachment2 = UUID.randomUUID();
    	AppUser appUser = AppUser.builder().userId("userId").build();
    	DecisionNotification decisionNotification = DecisionNotification.builder()
    			.signatory("sign")
    			.operators(Set.of("oper"))
    			.build();
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder().build();
    	Request request = Request.builder()
    			.payload(requestPayload)
    			.build();
    	PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
    			.permitSectionsCompleted(Map.of("sec1", List.of(true)))
    			.permitVariationDetailsCompleted(true)
    			.permitAttachments(Map.of(attachment1, "attachment1"))
    			.reviewSectionsCompleted(Map.of("reviewSec1", true))
    			.permitVariationDetailsReviewCompleted(true)
    			.reviewGroupDecisions(Map.of(PermitReviewGroup.ADDITIONAL_INFORMATION, PermitVariationReviewDecision.builder()
    					.details(ReviewDecisionDetails.builder()
    							.notes("notes")
    							.build())
    					.build()))
    			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
    					.details(ReviewDecisionDetails.builder()
    							.notes("notes")
    							.build())
    					.build())
    			.reviewAttachments(Map.of(attachment2, "attachment2"))
    			.determination(PermitVariationGrantDetermination.builder()
    					.reason("reason")
    					.build())
    			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();
    	
    	cut.savePermitVariationDecisionNotification(requestTask, decisionNotification, appUser);
    	
    	assertThat(requestPayload).isEqualTo(PermitVariationRequestPayload.builder()
    			.decisionNotification(decisionNotification)
    			.regulatorReviewer(appUser.getUserId())
    			.permitType(requestTaskPayload.getPermitType())
    			.permit(requestTaskPayload.getPermit())
    			.permitVariationDetails(requestTaskPayload.getPermitVariationDetails())
    			.permitSectionsCompleted(requestTaskPayload.getPermitSectionsCompleted())
    			.permitVariationDetailsCompleted(requestTaskPayload.getPermitVariationDetailsCompleted())
    			.permitAttachments(requestTaskPayload.getPermitAttachments())
    			.reviewSectionsCompleted(requestTaskPayload.getReviewSectionsCompleted())
    			.permitVariationDetailsReviewCompleted(requestTaskPayload.getPermitVariationDetailsReviewCompleted())
    			.reviewGroupDecisions(requestTaskPayload.getReviewGroupDecisions())
    			.permitVariationDetailsReviewDecision(requestTaskPayload.getPermitVariationDetailsReviewDecision())
    			.reviewAttachments(requestTaskPayload.getReviewAttachments())
    			.determination(requestTaskPayload.getDetermination())
    			.build());
    }

    @Test
    void saveRequestPeerReviewAction() {

        final UUID attachment1 = UUID.randomUUID();
        final UUID attachment2 = UUID.randomUUID();
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder().build();
        final Request request = Request.builder()
            .payload(requestPayload)
            .build();
        final PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload =
            PermitVariationApplicationReviewRequestTaskPayload.builder()
                .permitType(PermitType.GHGE)
                .permit(Permit.builder()
                    .abbreviations(Abbreviations.builder().exist(true).build())
                    .build())
                .permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
                .permitSectionsCompleted(Map.of("sec1", List.of(true)))
                .permitVariationDetailsCompleted(true)
                .permitAttachments(Map.of(attachment1, "attachment1"))
                .reviewSectionsCompleted(Map.of("reviewSec1", true))
                .permitVariationDetailsReviewCompleted(true)
                .reviewGroupDecisions(
                    Map.of(PermitReviewGroup.ADDITIONAL_INFORMATION, PermitVariationReviewDecision.builder()
                        .details(ReviewDecisionDetails.builder()
                            .notes("notes")
                            .build())
                        .build()))
                .permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
                    .details(ReviewDecisionDetails.builder()
                        .notes("notes")
                        .build())
                    .build())
                .reviewAttachments(Map.of(attachment2, "attachment2"))
                .determination(PermitVariationGrantDetermination.builder()
                    .reason("reason")
                    .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(requestTaskPayload)
            .build();

        final String peerReviewer = "peerReviewer";

        cut.saveRequestPeerReviewAction(requestTask, peerReviewer, appUser.getUserId());

        assertThat(requestPayload).isEqualTo(PermitVariationRequestPayload.builder()
            .regulatorPeerReviewer(peerReviewer)
            .regulatorReviewer(appUser.getUserId())
            .permitType(requestTaskPayload.getPermitType())
            .permit(requestTaskPayload.getPermit())
            .permitVariationDetails(requestTaskPayload.getPermitVariationDetails())
            .permitSectionsCompleted(requestTaskPayload.getPermitSectionsCompleted())
            .permitVariationDetailsCompleted(requestTaskPayload.getPermitVariationDetailsCompleted())
            .permitAttachments(requestTaskPayload.getPermitAttachments())
            .reviewSectionsCompleted(requestTaskPayload.getReviewSectionsCompleted())
            .permitVariationDetailsReviewCompleted(requestTaskPayload.getPermitVariationDetailsReviewCompleted())
            .reviewGroupDecisions(requestTaskPayload.getReviewGroupDecisions())
            .permitVariationDetailsReviewDecision(requestTaskPayload.getPermitVariationDetailsReviewDecision())
            .reviewAttachments(requestTaskPayload.getReviewAttachments())
            .determination(requestTaskPayload.getDetermination())
            .build());
    }
    
}

