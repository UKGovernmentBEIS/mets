package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

class EmpIssuanceCorsiaReviewRequestActionMapperTest {

    private final EmpIssuanceCorsiaReviewRequestActionMapper requestActionMapper = Mappers.getMapper(EmpIssuanceCorsiaReviewRequestActionMapper.class);

    @Test
    void cloneApprovedPayloadIgnoreReasonAndDecisions() {
        EmissionsMonitoringPlanCorsia emissionsMonitoringPlan = EmissionsMonitoringPlanCorsia.builder().build();
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("reason")
            .build();
        Map<EmpCorsiaReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpCorsiaReviewGroup.OPERATOR_DETAILS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        );
        EmpIssuanceCorsiaApplicationApprovedRequestActionPayload empIssuanceApplicationApprovedRequestActionPayload =
            EmpIssuanceCorsiaApplicationApprovedRequestActionPayload.builder()
                        .emissionsMonitoringPlan(emissionsMonitoringPlan)
                        .determination(determination)
                        .reviewGroupDecisions(reviewGroupDecisions)
                        .empDocument(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("emp document name").build())
                        .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("approved official notice name").build())
                .build();

        EmpIssuanceCorsiaApplicationApprovedRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneApprovedPayloadIgnoreReasonAndDecisions(empIssuanceApplicationApprovedRequestActionPayload);

        assertEquals(emissionsMonitoringPlan, clonedRequestActionPayload.getEmissionsMonitoringPlan());
        assertThat(clonedRequestActionPayload.getReviewGroupDecisions()).isEmpty();

        EmpIssuanceDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }

    @Test
    void cloneDeemedWithdrawnPayloadIgnoreReason() {
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN)
            .reason("reason")
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operatorUser"))
            .signatory("regulatorUser")
            .build();
        EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload
            empIssuanceApplicationDeemedWithdrawnRequestActionPayload =
            EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload.builder()
                        .determination(determination)
                        .decisionNotification(decisionNotification)
                        .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("approved official notice name").build())
                .build();

        EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneDeemedWithdrawnPayloadIgnoreReason(empIssuanceApplicationDeemedWithdrawnRequestActionPayload);

        assertEquals(decisionNotification, clonedRequestActionPayload.getDecisionNotification());

        EmpIssuanceDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }
}