package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service.EmpVariationUkEtsSubmitUploadAttachmentService;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsSectionUploadAttachmentHandlerTest {

	@InjectMocks
    private EmpVariationUkEtsSectionUploadAttachmentHandler handler;
    
    @Mock
    private EmpVariationUkEtsSubmitUploadAttachmentService uploadAttachmentService;
    
    @Test
    void uploadAttachment() {
        Long requestTaskId = 1L;
        String filename = "filename";
        String attachmentUuid = UUID.randomUUID().toString();

        //invoke
        handler.uploadAttachment(requestTaskId, attachmentUuid, filename);

        verify(uploadAttachmentService, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, filename);
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getType()).isEqualTo(RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT);
    }
    
}
