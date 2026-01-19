package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRRegulatorReviewUploadAttachmentService;

@Component
@RequiredArgsConstructor
public class WasteQDRRegulatorReviewUploadAttachmentHandler  extends RequestTaskUploadAttachmentActionHandler {

    private final WasteQDRRegulatorReviewUploadAttachmentService uploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        uploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.WASTE_QDR_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT;
    }
}
