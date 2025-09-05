package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIDeemedWithdrawnCustomMapperTest {

    @InjectMocks
    private HSETIDeemedWithdrawnCustomMapper mapper;

    @Mock
    private HSETICompletedCustomMapper hsetiCompletedCustomMapper;


    @Test
    void toRequestActionDTO() {
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


        RequestActionDTO expectedResult = RequestActionDTO.builder().payload(requestActionPayload).build();

        when(hsetiCompletedCustomMapper.toRequestActionDTO(requestAction)).thenReturn(expectedResult);

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isEqualTo(expectedResult);
        verify(hsetiCompletedCustomMapper, times(1)).toRequestActionDTO(requestAction);
    }

    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.HSE_TI_DEEMED_WITHDRAWN);
    }

    @Test
    public void getUserRoleTypes(){
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
