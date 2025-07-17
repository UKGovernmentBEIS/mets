package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditSubmitNotifyOperatorActionHandlerTest {

    @InjectMocks
    private InstallationAuditSubmitNotifyOperatorActionHandler handler;

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR);
    }

    @Test
    void getRequestTaskPayloadType() {
        assertThat(handler.getSubmitOutcomeBpmnConstantKey()).isEqualTo(BpmnProcessConstants.INSTALLATION_AUDIT_SUBMIT_OUTCOME);
    }
}
