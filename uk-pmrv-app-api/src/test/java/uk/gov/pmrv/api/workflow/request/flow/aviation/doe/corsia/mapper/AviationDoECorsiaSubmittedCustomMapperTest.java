package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmittedCustomMapperTest {

    @InjectMocks
    private AviationDoECorsiaSubmittedCustomMapper cut;

    @Test
    void toRequestActionDTO() {
        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED)
                .payload(AviationDoECorsiaSubmittedRequestActionPayload.builder()
                        .doe(AviationDoECorsia.builder()
                                .emissions(AviationDoECorsiaEmissions.builder().emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TEN).build())
                                .fee(AviationDoECorsiaFee.builder().feeDetails(AviationDoECorsiaFeeDetails.builder().build()).build())
                                .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                                        .type(
                                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                                        .furtherDetails("furtherDetails")
                                        .build())
                                .build())
                        .officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("filename").build())
                        .build())
                .build();

        RequestActionDTO result = cut.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        assertThat(result.getPayload()).isInstanceOf(AviationDoECorsiaSubmittedRequestActionPayload.class);

        AviationDoECorsiaSubmittedRequestActionPayload resultPayload =
                (AviationDoECorsiaSubmittedRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getDoe().getDeterminationReason().getFurtherDetails()).isNull();
        assertThat(resultPayload.getDoe().getDeterminationReason().getType())
                .isEqualTo(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT);
        assertThat(resultPayload.getDoe().getFee().getFeeDetails().getComments()).isNull();
    }

    @Test
    void getUserRoleTypes() {
        assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR);
    }
}
