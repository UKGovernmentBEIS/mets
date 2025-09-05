package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionAcceptedDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIReviewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HSETIRegulatorReturnedForAmendsCustomMapperTest {

    @InjectMocks
    private HSETIRegulatorReturnedForAmendsCustomMapper mapper;

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

        final HSETIRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = HSETIRegulatorReviewReturnedForAmendsRequestActionPayload
                .builder()
                .regulatorReviewGroupDecisions(regulatorGroupDecisions)
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS)
    			.payload(requestActionPayload)
    			.build();


        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        HSETIRegulatorReviewReturnedForAmendsRequestActionPayload resultPayload =
                (HSETIRegulatorReviewReturnedForAmendsRequestActionPayload) result.getPayload();

        assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(HSETIRegulatorReviewReturnedForAmendsRequestActionPayload.class);

        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getNotes()).isNull();
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getCapacityIncreaseDescription()).isNull();
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getCapacityGreaterThanZeroDescription()).isNull();
        assertThat(resultPayload.getRegulatorReviewGroupDecisions().get(HSETIReviewGroup.HSETI).getDetails().getCapacityIncreasePermanence()).isNull();
    }

    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.HSE_TI_REGULATOR_REVIEW_RETURNED_FOR_AMENDS);
    }

    @Test
    public void getUserRoleTypes(){
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }

}
