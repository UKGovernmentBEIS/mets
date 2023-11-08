package uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@ExtendWith(MockitoExtension.class)
class AirApplicationRespondedCustomMapperTest {

    @InjectMocks
    private AirApplicationRespondedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final LocalDateTime localDateTime = LocalDateTime.now();

        final RequestAction requestAction = RequestAction.builder()
            .id(11L)
            .type(RequestActionType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS)
            .submitter("operator")
            .creationDate(localDateTime)
            .payload(AirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .regulatorImprovementResponse(RegulatorAirImprovementResponse.builder().improvementRequired(true)
                    .comments("Comments 1").build()).build()
            )
            .request(Request.builder()
                .id("AIR")
                .accountId(1L)
                .type(RequestType.AIR)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build())
            .build();

        final RequestActionDTO expected = RequestActionDTO.builder()
            .id(11L)
            .type(RequestActionType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS)
            .submitter("operator")
            .creationDate(localDateTime)
            .requestId("AIR")
            .requestAccountId(1L)
            .requestType(RequestType.AIR)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .payload(AirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .regulatorImprovementResponse(RegulatorAirImprovementResponse.builder().improvementRequired(true)
                    .build()).build()
            )
            .build();

        // Invoke
        final RequestActionDTO actual = mapper.toRequestActionDTO(requestAction);

        // Verify
        assertThat(actual).isEqualTo(expected);
        assertThat(
            ((AirApplicationRespondedToRegulatorCommentsRequestActionPayload) requestAction.getPayload())
                .getRegulatorImprovementResponse().getComments()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(
            RequestActionType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
