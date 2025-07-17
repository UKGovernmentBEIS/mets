package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

@ExtendWith(MockitoExtension.class)
class AviationVirApplicationRespondedCustomMapperTest {

    @InjectMocks
    private AviationVirApplicationRespondedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final LocalDateTime localDateTime = LocalDateTime.now();

        final RequestAction requestAction = RequestAction.builder()
            .id(11L)
            .type(RequestActionType.AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS)
            .submitter("operator")
            .creationDate(localDateTime)
            .payload(AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .regulatorImprovementResponse(RegulatorImprovementResponse.builder().improvementRequired(true)
                    .improvementComments("Comments 1").build()).build()
            )
            .request(Request.builder()
                .id("VIR")
                .accountId(1L)
                .type(RequestType.AVIATION_VIR)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build())
            .build();

        final RequestActionDTO expected = RequestActionDTO.builder()
            .id(11L)
            .type(RequestActionType.AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS)
            .submitter("operator")
            .creationDate(localDateTime)
            .requestId("VIR")
            .requestAccountId(1L)
            .requestType(RequestType.AVIATION_VIR)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .payload(AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload.builder()
                .regulatorImprovementResponse(RegulatorImprovementResponse.builder().improvementRequired(true)
                    .build()).build()
            )
            .build();

        // Invoke
        final RequestActionDTO actual = mapper.toRequestActionDTO(requestAction);

        // Verify
        assertThat(actual).isEqualTo(expected);
        assertThat(
            ((AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload) requestAction.getPayload())
                .getRegulatorImprovementResponse().getImprovementComments()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(
            RequestActionType.AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
