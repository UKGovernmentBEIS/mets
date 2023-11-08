package uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper;

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
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondedToRegulatorCommentsRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class VirApplicationRespondedCustomMapperTest {

    @InjectMocks
    private VirApplicationRespondedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final LocalDateTime localDateTime = LocalDateTime.now();

        final RequestAction requestAction = RequestAction.builder()
            .id(11L)
            .type(RequestActionType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS)
            .submitter("operator")
            .creationDate(localDateTime)
            .payload(VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .regulatorImprovementResponse(RegulatorImprovementResponse.builder().improvementRequired(true)
                    .improvementComments("Comments 1").build()).build()
            )
            .request(Request.builder()
                .id("VIR")
                .accountId(1L)
                .type(RequestType.VIR)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build())
            .build();

        final RequestActionDTO expected = RequestActionDTO.builder()
            .id(11L)
            .type(RequestActionType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS)
            .submitter("operator")
            .creationDate(localDateTime)
            .requestId("VIR")
            .requestAccountId(1L)
            .requestType(RequestType.VIR)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .payload(VirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .regulatorImprovementResponse(RegulatorImprovementResponse.builder().improvementRequired(true)
                    .build()).build()
            )
            .build();

        // Invoke
        final RequestActionDTO actual = mapper.toRequestActionDTO(requestAction);

        // Verify
        assertThat(actual).isEqualTo(expected);
        assertThat(
            ((VirApplicationRespondedToRegulatorCommentsRequestActionPayload) requestAction.getPayload())
                .getRegulatorImprovementResponse().getImprovementComments()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(
            RequestActionType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
