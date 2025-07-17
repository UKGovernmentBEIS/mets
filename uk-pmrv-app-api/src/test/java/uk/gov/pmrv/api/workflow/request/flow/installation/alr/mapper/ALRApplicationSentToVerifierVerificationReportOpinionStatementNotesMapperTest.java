package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationSentToVerifierVerificationReportOpinionStatementNotesMapperTest {

    @InjectMocks
    private ALRApplicationSentToVerifierVerificationReportOpinionStatementNotesMapper mapper;

    @Test
    public void toRequestActionDTO() {

        final ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload
                .builder()
                .verificationReport(ALRVerificationReport
                        .builder()
                        .verificationData(ALRVerificationData
                                .builder()
                                .opinionStatement(ALRVerificationOpinionStatement
                                        .builder()
                                        .notes("Test")
                                        .build())
                                .build())
                        .build())
                .build();

        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER)
                .payload(requestActionPayload)
                .build();

        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        assertThat(result.getPayload()).isInstanceOf(ALRApplicationSubmittedRequestActionPayload.class);
        assertThat(((ALRApplicationSubmittedRequestActionPayload) result.getPayload()).getVerificationReport()).isNull();
    }

    @Test
    public void getRequestActionType() {

        RequestActionType requestActionType = mapper.getRequestActionType();

        assertThat(requestActionType).isEqualTo(RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER);
    }

    @Test
    public void getUserRoleTypes() {
        Set<String> roleTypes = mapper.getUserRoleTypes();

        assertThat(roleTypes).hasSize(1);
        assertThat(roleTypes).containsExactly(RoleTypeConstants.OPERATOR);
    }
}
