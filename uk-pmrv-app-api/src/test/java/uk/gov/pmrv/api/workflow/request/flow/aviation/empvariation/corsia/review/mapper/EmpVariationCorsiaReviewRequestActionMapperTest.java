package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationRejectedRequestActionPayload;

class EmpVariationCorsiaReviewRequestActionMapperTest {

	private final EmpVariationCorsiaReviewRequestActionMapper requestActionMapper = 
			Mappers.getMapper(EmpVariationCorsiaReviewRequestActionMapper.class);

    @Test
    void cloneApprovedPayloadIgnoreReasonAndDecisions() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder().build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder()
            .type(EmpVariationDeterminationType.APPROVED)
            .reason("reason")
            .build();
        Map<EmpCorsiaReviewGroup, EmpVariationReviewDecision> reviewGroupDecisions = Map.of(
        	EmpCorsiaReviewGroup.OPERATOR_DETAILS, EmpVariationReviewDecision.builder()
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
        EmpVariationCorsiaApplicationApprovedRequestActionPayload requestActionPayload =
        		EmpVariationCorsiaApplicationApprovedRequestActionPayload.builder()
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

        EmpVariationCorsiaApplicationApprovedRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneApprovedPayloadIgnoreReasonAndDecisionsNotes(requestActionPayload);

        assertEquals(emissionsMonitoringPlan, clonedRequestActionPayload.getEmissionsMonitoringPlan());
        assertThat(clonedRequestActionPayload.getReviewGroupDecisions()).containsExactlyEntriesOf(Map.of(
            EmpCorsiaReviewGroup.OPERATOR_DETAILS, EmpVariationReviewDecision.builder()
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
		EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
			EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload.builder()
				.determination(determination)
				.officialNotice(FileInfoDTO.builder()
					.uuid(UUID.randomUUID().toString())
					.name("withdrawn official notice name")
					.build())
				.build();

		EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
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
		EmpVariationCorsiaApplicationRejectedRequestActionPayload requestActionPayload =
			EmpVariationCorsiaApplicationRejectedRequestActionPayload.builder()
				.determination(determination)
				.officialNotice(FileInfoDTO.builder()
					.uuid(UUID.randomUUID().toString())
					.name("withdrawn official notice name")
					.build())
				.build();

        EmpVariationCorsiaApplicationRejectedRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneRejectedPayloadIgnoreReason(requestActionPayload);

        EmpVariationDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }
}
