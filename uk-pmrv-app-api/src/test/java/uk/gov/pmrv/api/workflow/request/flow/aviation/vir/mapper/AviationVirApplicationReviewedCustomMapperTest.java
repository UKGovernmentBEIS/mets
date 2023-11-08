package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;

@ExtendWith(MockitoExtension.class)
class AviationVirApplicationReviewedCustomMapperTest {

    @InjectMocks
    private AviationVirApplicationReviewedCustomMapper mapper;

    @Test
    void toRequestActionDTO() {
        
        LocalDateTime localDateTime = LocalDateTime.now();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("recommended_improvements.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

        RequestAction requestAction = RequestAction.builder()
                .id(11L)
                .type(RequestActionType.AVIATION_VIR_APPLICATION_REVIEWED)
                .submitter("operator")
                .creationDate(localDateTime)
                .payload(AviationVirApplicationReviewedRequestActionPayload.builder()
                        .regulatorReviewResponse(RegulatorReviewResponse.builder()
                                .regulatorImprovementResponses(Map.of(
                                        "A1", RegulatorImprovementResponse.builder().improvementRequired(true).improvementComments("Comments 1").build(),
                                        "A2", RegulatorImprovementResponse.builder().improvementRequired(true).improvementComments("Comments 2").build()
                                ))
                                .reportSummary("reportSummary")
                                .build())
                        .officialNotice(officialNotice)
                        .build())
                .request(Request.builder()
                        .id("AEM")
                        .accountId(1L)
                        .type(RequestType.VIR)
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build())
                .build();

        RequestActionDTO expected = RequestActionDTO.builder()
                .id(11L)
                .type(RequestActionType.AVIATION_VIR_APPLICATION_REVIEWED)
                .submitter("operator")
                .creationDate(localDateTime)
                .requestId("AEM")
                .requestAccountId(1L)
                .requestType(RequestType.VIR)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .payload(AviationVirApplicationReviewedRequestActionPayload.builder()
                        .regulatorReviewResponse(RegulatorReviewResponse.builder()
                                .regulatorImprovementResponses(Map.of(
                                        "A1", RegulatorImprovementResponse.builder().improvementRequired(true).build(),
                                        "A2", RegulatorImprovementResponse.builder().improvementRequired(true).build()
                                ))
                                .reportSummary("reportSummary")
                                .build())
                        .officialNotice(officialNotice)
                        .build())
                .build();

        // Invoke
        RequestActionDTO actual = mapper.toRequestActionDTO(requestAction);

        // Verify
        assertThat(actual).isEqualTo(expected);
        assertThat(((AviationVirApplicationReviewedRequestActionPayload)requestAction.getPayload())
            .getRegulatorReviewResponse().getRegulatorImprovementResponses().get("A1").getImprovementComments()).isNotNull();
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.AVIATION_VIR_APPLICATION_REVIEWED);
    }

    @Test
    void getUserRoleTypes() {
        assertThat(mapper.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
