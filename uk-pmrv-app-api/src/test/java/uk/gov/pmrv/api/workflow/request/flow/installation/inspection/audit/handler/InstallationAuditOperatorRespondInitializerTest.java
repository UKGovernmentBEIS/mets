package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditOperatorRespondInitializerTest {

    @InjectMocks
    private InstallationAuditOperatorRespondInitializer handler;


    @Test
    void getRequestTaskTypes(){
        assertThat(handler.getRequestTaskTypes())
                .containsExactlyInAnyOrder(RequestTaskType.INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS);
    }
}
