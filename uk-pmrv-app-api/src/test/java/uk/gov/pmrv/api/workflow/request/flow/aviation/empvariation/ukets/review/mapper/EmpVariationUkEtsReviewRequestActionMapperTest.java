package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationRejectedRequestActionPayload;

class EmpVariationUkEtsReviewRequestActionMapperTest {

	private final EmpVariationUkEtsReviewRequestActionMapper requestActionMapper = Mappers.getMapper(EmpVariationUkEtsReviewRequestActionMapper.class);

    @Test
    void cloneApprovedPayloadIgnoreReasonAndDecisions() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder().build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.APPROVED)
            .reason("reason")
            .build();
        Map<EmpUkEtsReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS, EmpVariationReviewDecision.builder()
    		.type(EmpVariationReviewDecisionType.ACCEPTED)
    		.details(EmpAcceptedVariationDecisionDetails.builder()
    				.notes("notes1")
    				.variationScheduleItems(List.of("item1", "item2"))
    				.build())
    		.build()
        );
        EmpVariationReviewDecision detailsReviewDecision = EmpVariationReviewDecision
        		.builder()
        		.type(EmpVariationReviewDecisionType.ACCEPTED)
        		.build();
        EmpVariationUkEtsApplicationApprovedRequestActionPayload requestActionPayload =
            EmpVariationUkEtsApplicationApprovedRequestActionPayload.builder()
                .emissionsMonitoringPlan(emissionsMonitoringPlan)
                .empVariationDetailsReviewDecision(detailsReviewDecision)
                .determination(determination)
                .reviewGroupDecisions(reviewGroupDecisions)
                .empVariationDetailsReviewDecision(EmpVariationReviewDecision.builder()
                		.type(EmpVariationReviewDecisionType.ACCEPTED)
                		.details(EmpAcceptedVariationDecisionDetails.builder()
                				.notes("notes3")
                				.variationScheduleItems(List.of("item3", "item4"))
                				.build())
                		.build())
                .empDocument(FileInfoDTO.builder()
                		.uuid(UUID.randomUUID().toString())
                		.name("emp document name")
                		.build())
                .officialNotice(FileInfoDTO.builder()
                		.uuid(UUID.randomUUID().toString())
                		.name("approved official notice name")
                		.build())
                .build();

        EmpVariationUkEtsApplicationApprovedRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneApprovedPayloadIgnoreReasonAndDecisionsNotes(requestActionPayload);

        assertEquals(emissionsMonitoringPlan, clonedRequestActionPayload.getEmissionsMonitoringPlan());
        assertThat(clonedRequestActionPayload.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS, EmpVariationReviewDecision.builder()
    		.type(EmpVariationReviewDecisionType.ACCEPTED)
    		.details(EmpAcceptedVariationDecisionDetails.builder()
    				.variationScheduleItems(List.of("item1", "item2"))
    				.build())
    		.build()
        ));
        assertThat(clonedRequestActionPayload.getEmpVariationDetailsReviewDecision()).isEqualTo(EmpVariationReviewDecision.builder()
                		.type(EmpVariationReviewDecisionType.ACCEPTED)
                		.details(EmpAcceptedVariationDecisionDetails.builder()
                				.variationScheduleItems(List.of("item3", "item4"))
                				.build())
                		.build());

        EmpVariationDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }

    @Test
    void cloneDeemedWithdrawnPayloadIgnoreReason() {
    	EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.DEEMED_WITHDRAWN)
            .reason("reason")
            .build();
        EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
        		EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload.builder()
                .determination(determination)
                .officialNotice(FileInfoDTO.builder()
                		.uuid(UUID.randomUUID().toString())
                		.name("withdrawn official notice name")
                		.build())
                .build();

        EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneDeemedWithdrawnPayloadIgnoreReason(requestActionPayload);

        EmpVariationDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }
    
    @Test
    void clonRejectedPayloadIgnoreReason() {
    	EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.REJECTED)
            .reason("reason")
            .build();
    	EmpVariationUkEtsApplicationRejectedRequestActionPayload requestActionPayload =
    			EmpVariationUkEtsApplicationRejectedRequestActionPayload.builder()
                .determination(determination)
                .officialNotice(FileInfoDTO.builder()
                		.uuid(UUID.randomUUID().toString())
                		.name("rejected official notice name")
                		.build())
                .build();

        EmpVariationUkEtsApplicationRejectedRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneRejectedPayloadIgnoreReason(requestActionPayload);

        EmpVariationDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }
    
    @Test
    void cloneApprovedPayloadIgnoreReasonAndDecisionsNotes() {
    	EmpVariationUkEtsApplicationApprovedRequestActionPayload sourcePayload = EmpVariationUkEtsApplicationApprovedRequestActionPayload.builder()
    			.determination(EmpVariationDetermination.builder()
    					.type(EmpVariationDeterminationType.APPROVED)
    					.reason("reason should be ignored")
    					.build())
    			.empVariationDetailsReviewDecision(EmpVariationReviewDecision.builder()
    					.type(EmpVariationReviewDecisionType.ACCEPTED)
    					.details(EmpAcceptedVariationDecisionDetails.builder()
    							.variationScheduleItems(List.of("item1", "item2"))
    							.notes("notes should be ignored")
    							.build())
    					.build())
    			.reviewGroupDecisions(Map.of(
    						EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
        					.type(EmpVariationReviewDecisionType.ACCEPTED)
        					.details(EmpAcceptedVariationDecisionDetails.builder()
        							.variationScheduleItems(List.of("item3", "item4"))
        							.notes("add notes should be ignored")
        							.build())
        					.build()
    					))
    			.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
    					.abbreviations(EmpAbbreviations.builder()
    							.exist(false)
    							.build())
    					.build())
    			.payloadType(RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_APPROVED_PAYLOAD)
    			.serviceContactDetails(ServiceContactDetails.builder()
    					.email("email")
    					.name("scd")
    					.build())
    			.build();
    	
    	EmpVariationUkEtsApplicationApprovedRequestActionPayload result = requestActionMapper.cloneApprovedPayloadIgnoreReasonAndDecisionsNotes(sourcePayload);
    	
    	assertThat(result.getDetermination()).isEqualTo(EmpVariationDetermination.builder()
    				.type(EmpVariationDeterminationType.APPROVED)
    					.build());
    	
    	assertThat(result.getEmpVariationDetailsReviewDecision()).isEqualTo(EmpVariationReviewDecision.builder()
				.type(EmpVariationReviewDecisionType.ACCEPTED)
				.details(EmpAcceptedVariationDecisionDetails.builder()
						.variationScheduleItems(List.of("item1", "item2"))
						.build())
				.build());
    	
    	assertThat(result.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
    			EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS, EmpVariationReviewDecision.builder()
    			.type(EmpVariationReviewDecisionType.ACCEPTED)
        					.details(EmpAcceptedVariationDecisionDetails.builder()
        							.variationScheduleItems(List.of("item3", "item4"))
        							.build())
        					.build()));
    	
    	assertThat(result.getEmissionsMonitoringPlan()).isEqualTo(sourcePayload.getEmissionsMonitoringPlan());
    	assertThat(result.getServiceContactDetails()).isEqualTo(sourcePayload.getServiceContactDetails());
    }
}
