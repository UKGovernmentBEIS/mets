package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFeeDetails;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsApplicationSubmittedCustomMapperTest {

    @InjectMocks
    private AviationDreUkEtsApplicationSubmittedCustomMapper cut;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED)
            .payload(AviationDreApplicationSubmittedRequestActionPayload.builder()
                .dre(AviationDre.builder()
                    .determinationReason(AviationDreDeterminationReason.builder()
                        .furtherDetails("furtherDetails")
                        .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                        .build())
                    .fee(AviationDreFee.builder()
                        .chargeOperator(true)
                        .feeDetails(AviationDreFeeDetails.builder()
                            .comments("comments")
                            .hourlyRate(BigDecimal.valueOf(1000))
                            .build())
                        .build())
                    .build())
                .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("filename").build())
                .build())
            .build();

        RequestActionDTO result = cut.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        assertThat(result.getPayload()).isInstanceOf(AviationDreApplicationSubmittedRequestActionPayload.class);

        AviationDreApplicationSubmittedRequestActionPayload resultPayload =
            (AviationDreApplicationSubmittedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getDre().getDeterminationReason().getFurtherDetails()).isNull();
        assertThat(resultPayload.getDre().getDeterminationReason().getType())
            .isEqualTo(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT);
        assertThat(resultPayload.getDre().getFee().getFeeDetails().getComments()).isNull();
    }

    @Test
    void getUserRoleTypes() {
        assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}