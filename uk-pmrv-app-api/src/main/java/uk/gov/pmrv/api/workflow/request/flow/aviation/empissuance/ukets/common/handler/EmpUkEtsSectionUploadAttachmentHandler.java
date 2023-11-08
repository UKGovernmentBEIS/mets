package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpUkEtsSectionUploadAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandler;

@Component
@RequiredArgsConstructor
public class EmpUkEtsSectionUploadAttachmentHandler extends RequestTaskUploadAttachmentActionHandler  {

    private final EmpUkEtsSectionUploadAttachmentService empUkEtsSectionUploadAttachmentService;

    @Override
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        empUkEtsSectionUploadAttachmentService.uploadAttachment(requestTaskId, attachmentUuid, filename);
    }

    @Override
    public RequestTaskActionType getType() {
        return RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_SECTION_ATTACHMENT;
    }
}
