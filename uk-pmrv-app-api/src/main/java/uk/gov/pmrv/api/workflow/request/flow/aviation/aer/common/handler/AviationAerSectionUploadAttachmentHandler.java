package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerSectionUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class AviationAerSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler {

    private final AviationAerSectionUploadAttachmentService aviationAerSectionUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        aviationAerSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.AVIATION_AER_UPLOAD_SECTION_ATTACHMENT;
    }
}
