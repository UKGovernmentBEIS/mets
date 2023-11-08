package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.service.EmpVariationUkEtsSubmitUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler  {
	
	private final EmpVariationUkEtsSubmitUploadAttachmentService empVariationUploadAttachmentService;
    
    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
    	empVariationUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT;
    }
}
