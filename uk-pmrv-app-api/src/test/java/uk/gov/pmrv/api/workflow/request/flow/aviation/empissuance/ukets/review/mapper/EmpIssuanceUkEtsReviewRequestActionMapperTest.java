package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmpIssuanceUkEtsReviewRequestActionMapperTest {

    private final EmpIssuanceUkEtsReviewRequestActionMapper requestActionMapper = Mappers.getMapper(EmpIssuanceUkEtsReviewRequestActionMapper.class);

    @Test
    void cloneApprovedPayloadIgnoreReasonAndDecisions() {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = EmissionsMonitoringPlanUkEts.builder().build();
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder()
            .type(EmpIssuanceDeterminationType.APPROVED)
            .reason("reason")
            .build();
        Map<EmpUkEtsReviewGroup, EmpIssuanceReviewDecision> reviewGroupDecisions = Map.of(
            EmpUkEtsReviewGroup.OPERATOR_DETAILS, EmpIssuanceReviewDecision.builder().type(EmpReviewDecisionType.ACCEPTED).build()
        );
        EmpIssuanceUkEtsApplicationApprovedRequestActionPayload empIssuanceApplicationApprovedRequestActionPayload =
                EmpIssuanceUkEtsApplicationApprovedRequestActionPayload.builder()
                        .emissionsMonitoringPlan(emissionsMonitoringPlan)
                        .determination(determination)
                        .reviewGroupDecisions(reviewGroupDecisions)
                        .empDocument(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("emp document name").build())
                        .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("approved official notice name").build())
                        .build();

        EmpIssuanceUkEtsApplicationApprovedRequestActionPayload clonedRequestActionPayload =
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
        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload empIssuanceApplicationDeemedWithdrawnRequestActionPayload =
                EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload.builder()
                        .determination(determination)
                        .decisionNotification(decisionNotification)
                        .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("approved official notice name").build())
                        .build();

        EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload clonedRequestActionPayload =
            requestActionMapper.cloneDeemedWithdrawnPayloadIgnoreReason(empIssuanceApplicationDeemedWithdrawnRequestActionPayload);

        assertEquals(decisionNotification, clonedRequestActionPayload.getDecisionNotification());

        EmpIssuanceDetermination clonedRequestActionPayloadDetermination = clonedRequestActionPayload.getDetermination();
        assertEquals(determination.getType(), clonedRequestActionPayloadDetermination.getType());
        assertNull(clonedRequestActionPayloadDetermination.getReason());
    }
}