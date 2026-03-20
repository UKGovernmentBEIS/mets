package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Bdrs2DataRegulatorReviewRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ReviewGroup;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationReturnForAmendsWithoutInternalNotesMapperTest {

    @InjectMocks
    private BDRS2ApplicationReturnForAmendsWithoutInternalNotesMapper mapper;

    @Test
    void toRequestActionDTO_operator_shouldNotSeeNotes() {
        final RequestAction requestAction = buildRequestAction();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        final BDRS2Bdrs2DataRegulatorReviewDecision resultDecision = getDecision(result);

        assertThat(resultDecision.getDetails().getNotes()).isNull();
        assertThat(resultDecision.getType()).isEqualTo(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED);
        assertThat(((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails) resultDecision.getDetails())
                .getVerificationRequired()).isFalse();
        assertThat(((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails) resultDecision.getDetails())
                .getRequiredChanges()).hasSize(1);
    }

    @Test
    void regulator_shouldSeeNotes() {
        final String regulatorNotes = "regulator internal notes";
        final RequestAction requestAction = buildRequestAction();

        final BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload payload =
                (BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload) requestAction.getPayload();
        final BDRS2Bdrs2DataRegulatorReviewDecision decision =
                (BDRS2Bdrs2DataRegulatorReviewDecision) payload.getRegulatorReviewGroupDecisions().get(BDRS2ReviewGroup.BDRS2);

        assertThat(decision.getDetails().getNotes()).isEqualTo(regulatorNotes);
        assertThat(((BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails) decision.getDetails())
                .getRequiredChanges()).hasSize(1);
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType())
                .isEqualTo(RequestActionType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS);
    }

    private RequestAction buildRequestAction() {
        return RequestAction.builder()
                .type(RequestActionType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS)
                .payload(BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.BDRS2_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD)
                        .regulatorReviewGroupDecisions(new EnumMap<>(Map.of(
                                BDRS2ReviewGroup.BDRS2,
                                BDRS2Bdrs2DataRegulatorReviewDecision.builder()
                                        .reviewDataType(BDRS2ReviewDataType.BDRS2_DATA)
                                        .type(BDRS2Bdrs2DataRegulatorReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                                        .details(BDRS2Bdrs2DataRegulatorReviewOperatorAmendsNeededDecisionDetails.builder()
                                                .notes("regulator internal notes")
                                                .verificationRequired(false)
                                                .requiredChanges(List.of(
                                                        BDRS2Bdrs2DataRegulatorReviewRequiredChange.builder()
                                                                .reason("Required change 1")
                                                                .build()))
                                                .build())
                                        .build()
                        )))
                        .build())
                .build();
    }

    private BDRS2Bdrs2DataRegulatorReviewDecision getDecision(RequestActionDTO dto) {
        final BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload payload =
                (BDRS2RegulatorReviewReturnedForAmendsRequestActionPayload) dto.getPayload();
        return (BDRS2Bdrs2DataRegulatorReviewDecision) payload.getRegulatorReviewGroupDecisions().get(BDRS2ReviewGroup.BDRS2);
    }
}
