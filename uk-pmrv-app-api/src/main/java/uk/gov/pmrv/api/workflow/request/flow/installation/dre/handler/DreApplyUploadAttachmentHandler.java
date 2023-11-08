package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class DreApplyUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {
	
	private final DreApplyUploadAttachmentService dreApplyUploadAttachmentService;

	@Override
	public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
		dreApplyUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
	}

	@Override
	public RequestTaskActionType getType() {
		return RequestTaskActionType.DRE_APPLY_UPLOAD_ATTACHMENT;
	}

}
