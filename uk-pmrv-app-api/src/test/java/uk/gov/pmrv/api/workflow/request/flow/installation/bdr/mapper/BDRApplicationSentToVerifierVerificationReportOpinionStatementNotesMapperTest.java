package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationSentToVerifierVerificationReportOpinionStatementNotesMapperTest {

    @InjectMocks
    private BDRApplicationSentToVerifierVerificationReportOpinionStatementNotesMapper mapper;

    @Test
    public void toRequestActionDTO() {

        final BDRApplicationSubmittedRequestActionPayload requestActionPayload = BDRApplicationSubmittedRequestActionPayload
                .builder()
                .verificationReport(BDRVerificationReport
                        .builder()
                        .verificationData(BDRVerificationData
                                .builder()
                                .opinionStatement(BDRVerificationOpinionStatement
                                        .builder()
                                        .notes("Test")
                                        .build())
                          .build())
                        .build())
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.BDR_APPLICATION_SENT_TO_VERIFIER)
    			.payload(requestActionPayload)
    			.build();


        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(BDRApplicationSubmittedRequestActionPayload.class);
    	assertThat(((BDRApplicationSubmittedRequestActionPayload) result.getPayload()).getVerificationReport()).isNull();
    }

    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.BDR_APPLICATION_SENT_TO_VERIFIER);
    }

    @Test
    public void getUserRoleTypes(){
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
