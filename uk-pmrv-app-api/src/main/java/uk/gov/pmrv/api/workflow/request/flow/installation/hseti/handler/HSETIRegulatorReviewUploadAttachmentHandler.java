package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIRegulatorReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class HSETIRegulatorReviewUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final HSETIRegulatorReviewUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.HSE_TI_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT;
    }

}
