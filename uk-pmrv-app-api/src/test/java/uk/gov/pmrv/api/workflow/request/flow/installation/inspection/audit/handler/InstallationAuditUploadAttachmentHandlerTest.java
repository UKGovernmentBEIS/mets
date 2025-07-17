package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditUploadAttachmentHandlerTest {

    @InjectMocks
    private InstallationAuditUploadAttachmentHandler handler;

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.INSTALLATION_AUDIT_UPLOAD_ATTACHMENT);
    }
}
