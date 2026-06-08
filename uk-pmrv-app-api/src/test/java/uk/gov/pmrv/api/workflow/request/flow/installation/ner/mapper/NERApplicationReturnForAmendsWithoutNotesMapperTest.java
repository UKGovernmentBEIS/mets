package uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class NERApplicationReturnForAmendsWithoutNotesMapperTest {


    private final NERApplicationReturnForAmendsWithoutNotesMapper mapper =
            new NERApplicationReturnForAmendsWithoutNotesMapper();

    @Test
    void toRequestActionDTO() {
        NERNerDataRegulatorReviewAcceptedDecisionDetails nerDetails =
                NERNerDataRegulatorReviewAcceptedDecisionDetails.builder()
                        .notes("notes")
                        .build();

        NERNerDataRegulatorReviewDecision nerDecision =
                NERNerDataRegulatorReviewDecision.builder()
                        .reviewDataType(NERReviewDataType.NER_DATA)
                        .details(nerDetails)
                        .build();

        NERRegulatorReviewDecisionDetails vrDetails =
                NERRegulatorReviewDecisionDetails.builder()
                        .notes("notes")
                        .build();

        NERVerificationReportDataRegulatorReviewDecision vrDecision =
                NERVerificationReportDataRegulatorReviewDecision.builder()
                        .reviewDataType(NERReviewDataType.VERIFICATION_REPORT_DATA)
                        .details(vrDetails)
                        .build();

        NERRegulatorReviewReturnedForAmendsRequestActionPayload payload =
                NERRegulatorReviewReturnedForAmendsRequestActionPayload.builder()
                        .regulatorReviewGroupDecisions(Map.of(
                                NERReviewGroup.NER, nerDecision,
                                NERReviewGroup.OPINION_STATEMENT, vrDecision
                        ))
                        .build();

        RequestAction requestAction = new RequestAction();
        requestAction.setPayload(payload);

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        NERRegulatorReviewReturnedForAmendsRequestActionPayload resultPayload =
                (NERRegulatorReviewReturnedForAmendsRequestActionPayload) result.getPayload();

        NERNerDataRegulatorReviewDecision resultNerDecision =
                (NERNerDataRegulatorReviewDecision)
                        resultPayload.getRegulatorReviewGroupDecisions().get(NERReviewGroup.NER);

        NERVerificationReportDataRegulatorReviewDecision resultVrDecision =
                (NERVerificationReportDataRegulatorReviewDecision)
                        resultPayload.getRegulatorReviewGroupDecisions().get(NERReviewGroup.OPINION_STATEMENT);

        assertNull(resultNerDecision.getDetails().getNotes());
        assertNull(resultVrDecision.getDetails().getNotes());
    }

    @Test
    void getRequestActionType() {
        assertEquals(
                RequestActionType.NER_APPLICATION_RETURNED_FOR_AMENDS,
                mapper.getRequestActionType()
        );
    }

    @Test
    void getUserRoleTypes() {
        assertEquals(
                Set.of(RoleTypeConstants.OPERATOR),
                mapper.getUserRoleTypes()
        );
    }
}
