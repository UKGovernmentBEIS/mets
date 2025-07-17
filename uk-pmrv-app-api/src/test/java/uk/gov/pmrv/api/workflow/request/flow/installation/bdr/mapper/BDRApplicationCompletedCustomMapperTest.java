package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationCompletedCustomMapperTest {

    @InjectMocks
    private BDRApplicationCompletedCustomMapper mapper;


    @Test
    public void toRequestActionDTO() {
        UUID uuid = UUID.randomUUID();
        Set<UUID> c = Set.of(uuid);

        final BDRApplicationCompletedRequestActionPayload requestActionPayload = BDRApplicationCompletedRequestActionPayload
                .builder()
                .regulatorReviewOutcome(BDRApplicationRegulatorReviewOutcome
                        .builder()
                        .freeAllocationNotes("test")
                        .useHseNotes("test")
                        .freeAllocationNotesOperator("test operator")
                        .useHseNotesOperator("test operator")
                        .build())
                .verificationReport(BDRVerificationReport.builder()
                        .verificationData(BDRVerificationData.builder()
                                .opinionStatement(BDRVerificationOpinionStatement.builder()
                                        .notes("Must not be visible")
                                        .opinionStatementFiles(Set.of(uuid))
                        .build()).build()).build())
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.BDR_APPLICATION_COMPLETED)
    			.payload(requestActionPayload)
    			.build();


        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(BDRApplicationCompletedRequestActionPayload.class);
    	assertThat(((BDRApplicationCompletedRequestActionPayload) result.getPayload())
                .getRegulatorReviewOutcome()
                .getUseHseNotes()).isNull();
    	assertThat(((BDRApplicationCompletedRequestActionPayload) result.getPayload())
                .getRegulatorReviewOutcome()
                .getFreeAllocationNotes()).isNull();
        assertThat(((BDRApplicationCompletedRequestActionPayload) result.getPayload())
                .getRegulatorReviewOutcome()
                .getUseHseNotesOperator()).isNotNull();
        assertThat(((BDRApplicationCompletedRequestActionPayload) result.getPayload())
                .getRegulatorReviewOutcome()
                .getFreeAllocationNotesOperator()).isNotNull();

        BDRVerificationOpinionStatement opinionStatement = ((BDRApplicationCompletedRequestActionPayload) result.getPayload())
                .getVerificationReport().getVerificationData().getOpinionStatement();
        assertThat(opinionStatement.getNotes()).isNull();
        assertThat(opinionStatement.getOpinionStatementFiles()).isNotEmpty();
    }

    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.BDR_APPLICATION_COMPLETED);
    }

    @Test
    public void getUserRoleTypes(){
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
