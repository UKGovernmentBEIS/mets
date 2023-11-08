package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpUkEtsSectionUploadAttachmentService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmpUkEtsSectionUploadAttachmentHandlerTest {

    @InjectMocks
    private EmpUkEtsSectionUploadAttachmentHandler handler;

    @Mock
    private EmpUkEtsSectionUploadAttachmentService empUkEtsSectionUploadAttachmentService;

    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(empUkEtsSectionUploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_SECTION_ATTACHMENT);
    }
}