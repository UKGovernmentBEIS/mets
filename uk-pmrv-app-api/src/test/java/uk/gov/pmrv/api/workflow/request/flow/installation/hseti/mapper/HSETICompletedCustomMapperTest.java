package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETICompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionAcceptedDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewOverallDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HSETICompletedCustomMapperTest {

    @InjectMocks
    private HSETICompletedCustomMapper mapper;

    @Test
    public void toRequestActionDTO() {

        Map<HSETIReviewGroup, HSETIRegulatorReviewDecision> regulatorGroupDecisions = new HashMap<>();

        regulatorGroupDecisions.put(
                HSETIReviewGroup.HSETI,
                HSETIRegulatorReviewDecision
                .builder()
                        .type(HSETIRegulatorReviewDecisionType.ACCEPTED)
                        .details(HSETIRegulatorReviewDecisionAcceptedDetails
                                .builder()
                                .notes("Test notes")
                                .capacityGreaterThanZeroDescription("capacityGreaterThanZeroDescription")
                                .capacityIncreaseDescription("capacityIncreaseDescription")
                                .capacityIncreasePermanence("capacityIncreasePermanence")
                                .build())
                .build());


        final HSETICompletedRequestActionPayload requestActionPayload = HSETICompletedRequestActionPayload
                .builder()
                .overallDecision(HSETIRegulatorReviewOverallDecision
                        .builder()
                        .type(HSETIRegulatorReviewOverallDecisionType.APPROVED)
                        .reason("test reason")
                        .build())
                .regulatorReviewGroupDecisions(regulatorGroupDecisions)
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS)
    			.payload(requestActionPayload)
    			.build();


        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        HSETICompletedRequestActionPayload resultPayload = (HSETICompletedRequestActionPayload) result.getPayload();
        
        assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(HSETICompletedRequestActionPayload.class);
    	assertThat(resultPayload.getOverallDecision().getReason()).isNull();
     	assertThat(resultPayload.getOverallDecision().getType()).isEqualTo(HSETIRegulatorReviewOverallDecisionType.APPROVED);
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getNotes()).isNull();
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getCapacityIncreaseDescription()).isNull();
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getCapacityGreaterThanZeroDescription()).isNull();
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getCapacityIncreasePermanence()).isNull();
    }
}
