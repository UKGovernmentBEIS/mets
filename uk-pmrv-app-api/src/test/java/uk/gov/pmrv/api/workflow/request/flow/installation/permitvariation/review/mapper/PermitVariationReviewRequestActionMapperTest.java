package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRejectDetermination;

class PermitVariationReviewRequestActionMapperTest {

	private final PermitVariationReviewRequestActionMapper mapper = Mappers.getMapper(PermitVariationReviewRequestActionMapper.class);
	
	@Test
    void toPermitVariationApplicationGrantedRequestActionPayload() {
    	LocalDate activationDate = LocalDate.now().plusDays(1);
    	PermitContainer originalPermitContainer = PermitContainer.builder().permitType(PermitType.HSE).build();
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.originalPermitContainer(originalPermitContainer)
    			.determination(PermitVariationGrantDetermination.builder()
    					.type(DeterminationType.GRANTED)
    					.activationDate(activationDate)
    					.build())
    			.build();
    	
    	PermitVariationApplicationGrantedRequestActionPayload result = mapper.toPermitVariationApplicationGrantedRequestActionPayload(requestPayload);
    	
    	assertThat(result.getPermitType()).isEqualTo(PermitType.GHGE);
    	assertThat(result.getPermit()).isEqualTo(Permit.builder()
				.abbreviations(Abbreviations.builder().exist(true).build())
				.build());
    	assertThat(result.getDetermination()).isEqualTo(PermitVariationGrantDetermination.builder()
				.type(DeterminationType.GRANTED)
				.activationDate(activationDate)
				.build());
    	assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_GRANTED_PAYLOAD);
    	assertThat(result.getOriginalPermitContainer()).isEqualTo(originalPermitContainer);
    }
	
	@Test
    void toPermitVariationApplicationRejectedRequestActionPayload() {
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.determination(PermitVariationRejectDetermination.builder()
    					.type(DeterminationType.REJECTED)
    					.reason("reason")
    					.build())
    			.decisionNotification(DecisionNotification.builder()
    					.signatory("sign")
    					.build())
    			.build();
    	
    	PermitVariationApplicationRejectedRequestActionPayload result = mapper.toPermitVariationApplicationRejectedRequestActionPayload(requestPayload);
    	
    	assertThat(result.getDetermination().getType()).isEqualTo(DeterminationType.REJECTED);
    	assertThat(result.getDetermination()).isEqualTo(PermitVariationRejectDetermination.builder()
				.type(DeterminationType.REJECTED)
				.reason("reason")
				.build());
    	assertThat(result.getDecisionNotification()).isEqualTo(DecisionNotification.builder()
				.signatory("sign")
				.build());
    	assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_REJECTED_PAYLOAD);
    }
	
	@Test
    void toPermitVariationApplicationDeemedWithdrawnRequestActionPayload() {
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.determination(PermitVariationDeemedWithdrawnDetermination.builder()
    					.type(DeterminationType.DEEMED_WITHDRAWN)
    					.reason("reason")
    					.build())
    			.decisionNotification(DecisionNotification.builder()
    					.signatory("sign")
    					.build())
    			.build();
    	
    	PermitVariationApplicationDeemedWithdrawnRequestActionPayload result = mapper.toPermitVariationApplicationDeemedWithdrawnRequestActionPayload(requestPayload);
    	
    	assertThat(result.getDetermination().getType()).isEqualTo(DeterminationType.DEEMED_WITHDRAWN);
    	assertThat(result.getDetermination()).isEqualTo(PermitVariationDeemedWithdrawnDetermination.builder()
				.type(DeterminationType.DEEMED_WITHDRAWN)
				.reason("reason")
				.build());
    	assertThat(result.getDecisionNotification()).isEqualTo(DecisionNotification.builder()
				.signatory("sign")
				.build());
    	assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD);
    }
	
	@Test
    void cloneGrantedPayloadIgnoreReasonAndDecisionsNotes() {
    	PermitVariationApplicationGrantedRequestActionPayload sourcePayload = PermitVariationApplicationGrantedRequestActionPayload.builder()
    			.determination(PermitVariationGrantDetermination.builder()
    					.reason("reason should be ignored")
    					.logChanges("logChanges")
    					.build())
    			.permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
    					.type(ReviewDecisionType.ACCEPTED)
    					.details(PermitAcceptedVariationDecisionDetails.builder()
    							.variationScheduleItems(List.of("item1", "item2"))
    							.notes("notes should be ignored")
    							.build())
    					.build())
    			.reviewGroupDecisions(Map.of(
    						PermitReviewGroup.ADDITIONAL_INFORMATION, PermitVariationReviewDecision.builder()
        					.type(ReviewDecisionType.ACCEPTED)
        					.details(PermitAcceptedVariationDecisionDetails.builder()
        							.variationScheduleItems(List.of("item3", "item4"))
        							.notes("add notes should be ignored")
        							.build())
        					.build()
    					))
    			.permit(Permit.builder()
    					.additionalDocuments(AdditionalDocuments.builder()
    							.exist(false)
    							.build())
    					.build())
    			.payloadType(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_GRANTED_PAYLOAD)
    			.installationOperatorDetails(InstallationOperatorDetails.builder()
    					.companyReferenceNumber("compRef")
    					.build())
    			.build();
    	
    	PermitVariationApplicationGrantedRequestActionPayload result = mapper.cloneGrantedPayloadIgnoreReasonAndDecisionsNotes(sourcePayload);
    	
    	assertThat(result.getDetermination()).isEqualTo(PermitVariationGrantDetermination.builder()
    					.logChanges("logChanges")
    					.build());
    	
    	assertThat(result.getPermitVariationDetailsReviewDecision()).isEqualTo(PermitVariationReviewDecision.builder()
				.type(ReviewDecisionType.ACCEPTED)
				.details(PermitAcceptedVariationDecisionDetails.builder()
						.variationScheduleItems(List.of("item1", "item2"))
						.build())
				.build());
    	
    	assertThat(result.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
    						PermitReviewGroup.ADDITIONAL_INFORMATION, PermitVariationReviewDecision.builder()
        					.type(ReviewDecisionType.ACCEPTED)
        					.details(PermitAcceptedVariationDecisionDetails.builder()
        							.variationScheduleItems(List.of("item3", "item4"))
        							.build())
        					.build()));
    	
    	assertThat(result.getPermit()).isEqualTo(sourcePayload.getPermit());
    	assertThat(result.getInstallationOperatorDetails()).isEqualTo(sourcePayload.getInstallationOperatorDetails());
    }
}
