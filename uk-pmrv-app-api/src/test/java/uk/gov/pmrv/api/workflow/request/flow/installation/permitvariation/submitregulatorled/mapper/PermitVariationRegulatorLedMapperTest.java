package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

class PermitVariationRegulatorLedMapperTest {

    private final PermitVariationRegulatorLedMapper mapper = Mappers.getMapper(PermitVariationRegulatorLedMapper.class);
    
    @Test
    void toPermitVariationApplicationRegulatorLedApprovedRequestActionPayload() {
    	PermitContainer originalPermitContainer = PermitContainer.builder().permitType(PermitType.HSE).build();
    	LocalDate activationDate = LocalDate.now().plusDays(1);
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.originalPermitContainer(originalPermitContainer)
    			.permitType(PermitType.GHGE)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.permitVariationDetailsReviewDecisionRegulatorLed(PermitAcceptedVariationDecisionDetails.builder()
    					.notes("notes")
    					.build())
    			.determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
    					.reason("reason")
    					.activationDate(activationDate)
    					.build())
    			.reviewGroupDecisionsRegulatorLed(Map.of(PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
    					.notes("notes")
    					.build()))
    			.decisionNotification(DecisionNotification.builder()
    					.signatory("sign")
    					.build())
    			.build();
    	
    	PermitVariationApplicationRegulatorLedApprovedRequestActionPayload result = mapper.toPermitVariationApplicationRegulatorLedApprovedRequestActionPayload(requestPayload);
    	
    	assertThat(result.getPermitType()).isEqualTo(PermitType.GHGE);
    	assertThat(result.getPermit()).isEqualTo(Permit.builder()
				.abbreviations(Abbreviations.builder().exist(true).build())
				.build());
    	assertThat(result.getDetermination()).isEqualTo(PermitVariationRegulatorLedGrantDetermination.builder()
				.reason("reason")
				.activationDate(activationDate)
				.build());
    	assertThat(result.getReviewGroupDecisions()).containsAllEntriesOf(Map.of(PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
				.notes("notes")
				.build()));
    	assertThat(result.getPermitVariationDetailsReviewDecision()).isEqualTo(PermitAcceptedVariationDecisionDetails.builder()
    					.notes("notes")
    					.build());
    	assertThat(result.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD);
    	assertThat(result.getOriginalPermitContainer()).isEqualTo(originalPermitContainer);
    }

    @Test
    void toPermitVariationApplicationSubmitRegulatorLedRequestTaskPayload() {
    	PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
    			.determinationRegulatorLed(PermitVariationRegulatorLedGrantDetermination.builder()
    					.logChanges("logChanges")
    					.build())
    			.reviewGroupDecisionsRegulatorLed(Map.of(
    					PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder().notes("notes").build()
    					))
    			.permitVariationDetailsReviewDecisionRegulatorLed(PermitAcceptedVariationDecisionDetails.builder()
    					.notes("notes2")
    					.build())
    			.build();
    	
    	RequestTaskPayloadType payloadType = RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW_REGULATOR_LED_PAYLOAD;
    	
    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload result = mapper.toPermitVariationApplicationSubmitRegulatorLedRequestTaskPayload(requestPayload, payloadType);
    	assertThat(result.getPayloadType()).isEqualTo(payloadType);
    	assertThat(result.getDetermination()).isEqualTo(requestPayload.getDeterminationRegulatorLed());
    	assertThat(result.getReviewGroupDecisions()).isEqualTo(requestPayload.getReviewGroupDecisionsRegulatorLed());
    	assertThat(result.getPermitVariationDetailsReviewDecision()).isEqualTo(requestPayload.getPermitVariationDetailsReviewDecisionRegulatorLed());
    	
    }

    @Test
    void toPermitContainer_fromPermitVariationApplicationSubmitRegulatorLedRequestTaskPayload() {
    	final UUID attachment1 = UUID.randomUUID();
    	LocalDate activationDate = LocalDate.now();
    	final SortedMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", new BigDecimal(100));
    	PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload requestTaskPayload = PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
    			.permitType(PermitType.HSE)
    			.permit(Permit.builder()
                        .abbreviations(Abbreviations.builder().exist(true).build())
                        .build())
                .installationOperatorDetails(
                    InstallationOperatorDetails.builder().installationName("installationName1").build())
                .permitAttachments(Map.of(attachment1, "att1"))
                .determination(PermitVariationRegulatorLedGrantDetermination.builder()
    					.logChanges("logChanges")
    					.activationDate(activationDate)
    					.annualEmissionsTargets(annualEmissionsTargets)
    					.build())
    			.build(); 

        final PermitContainer result = mapper.toPermitContainer(requestTaskPayload);

        assertThat(result).isEqualTo(PermitContainer.builder()
            .permitType(requestTaskPayload.getPermitType())
            .permit(requestTaskPayload.getPermit())
            .installationOperatorDetails(requestTaskPayload.getInstallationOperatorDetails())
            .permitAttachments(requestTaskPayload.getPermitAttachments())
            .activationDate(activationDate)
            .annualEmissionsTargets(annualEmissionsTargets)
            .build());
    }
    
    @Test
    void cloneRegulatorLedApprovedPayloadIgnoreReasonAndDecisionNotes() {
    	PermitContainer originalPermitContainer = PermitContainer.builder().permitType(PermitType.HSE).build();
    	PermitVariationApplicationRegulatorLedApprovedRequestActionPayload fromPayload = PermitVariationApplicationRegulatorLedApprovedRequestActionPayload.builder()
    			.originalPermitContainer(originalPermitContainer)
    			.permit(Permit.builder()
    					.abbreviations(Abbreviations.builder().exist(true).build())
    					.build())
    			.determination(PermitVariationRegulatorLedGrantDetermination.builder()
    					.reason("reason")
    					.reasonTemplate(PermitVariationReasonTemplate.FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR)
    					.build())
    			.reviewGroupDecisions(Map.of(
    					PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
    					.notes("notes1")
    					.variationScheduleItems(List.of("sch1", "sch2"))
    					.build()
    					))
    			.permitVariationDetailsReviewDecision(PermitAcceptedVariationDecisionDetails.builder()
    					.notes("notes2")
    					.variationScheduleItems(List.of("sch3", "sch4"))
    					.build()
    					)
    			.build();
    	
    	PermitVariationApplicationRegulatorLedApprovedRequestActionPayload result = mapper.cloneRegulatorLedApprovedPayloadIgnoreReasonAndDecisionNotes(fromPayload);
    	assertThat(result.getDetermination().getReason()).isNull();
    	assertThat(result.getDetermination().getReasonTemplate()).isNotNull();
    	assertThat(result.getOriginalPermitContainer()).isEqualTo(originalPermitContainer);
    	assertThat(result.getReviewGroupDecisions()).containsAllEntriesOf(Map.of(
    			PermitReviewGroup.ADDITIONAL_INFORMATION, PermitAcceptedVariationDecisionDetails.builder()
				.variationScheduleItems(List.of("sch1", "sch2"))
				.build()
    			));
    	assertThat(result.getPermitVariationDetailsReviewDecision()).isEqualTo(PermitAcceptedVariationDecisionDetails.builder()
				.variationScheduleItems(List.of("sch3", "sch4"))
				.build()
				);
    }

}
