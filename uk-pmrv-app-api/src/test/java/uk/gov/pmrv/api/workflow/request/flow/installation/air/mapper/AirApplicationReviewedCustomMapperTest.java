package uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirReviewResponse;

@ExtendWith(MockitoExtension.class)
class AirApplicationReviewedCustomMapperTest {

    @InjectMocks
    private AirApplicationReviewedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {

        final LocalDateTime localDateTime = LocalDateTime.now();

        final String officialNotice = UUID.randomUUID().toString();
        final RequestAction requestAction = RequestAction.builder()
            .id(11L)
            .type(RequestActionType.AIR_APPLICATION_REVIEWED)
            .submitter("operator")
            .creationDate(localDateTime)
            .payload(AirApplicationReviewedRequestActionPayload.builder()
                .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(Map.of(
                        1, RegulatorAirImprovementResponse.builder().improvementRequired(true)
                            .comments("Comments 1").build(),
                        2, RegulatorAirImprovementResponse.builder().improvementRequired(true)
                            .comments("Comments 2").build()
                    ))
                    .reportSummary("reportSummary")
                    .build())
                .officialNotice(FileInfoDTO.builder().name("officialNotice").uuid(officialNotice).build())
                .build())
            .request(Request.builder()
                .id("AIR")
                .accountId(1L)
                .type(RequestType.AIR)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build())
            .build();

        final RequestActionDTO expected = RequestActionDTO.builder()
            .id(11L)
            .type(RequestActionType.AIR_APPLICATION_REVIEWED)
            .submitter("operator")
            .creationDate(localDateTime)
            .requestId("AIR")
            .requestAccountId(1L)
            .requestType(RequestType.AIR)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .payload(AirApplicationReviewedRequestActionPayload.builder()
                .regulatorReviewResponse(RegulatorAirReviewResponse.builder().regulatorImprovementResponses(Map.of(
                        1, RegulatorAirImprovementResponse.builder().improvementRequired(true).build(),
                        2, RegulatorAirImprovementResponse.builder().improvementRequired(true).build()
                    ))
                    .reportSummary("reportSummary")
                    .build())
                .officialNotice(FileInfoDTO.builder().name("officialNotice").uuid(officialNotice).build())
                .build())
            .build();

        // Invoke
        final RequestActionDTO actual = mapper.toRequestActionDTO(requestAction);

        // Verify
        assertThat(actual).isEqualTo(expected);
        assertThat(((AirApplicationReviewedRequestActionPayload)requestAction.getPayload()).getRegulatorReviewResponse().getRegulatorImprovementResponses().get(1).getComments()).isNotNull();
        assertThat(((AirApplicationReviewedRequestActionPayload)requestAction.getPayload()).getRegulatorReviewResponse().getRegulatorImprovementResponses().get(2).getComments()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.AIR_APPLICATION_REVIEWED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
