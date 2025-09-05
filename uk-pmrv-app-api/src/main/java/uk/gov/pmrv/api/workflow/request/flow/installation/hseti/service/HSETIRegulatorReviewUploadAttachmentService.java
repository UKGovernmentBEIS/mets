package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HSETIRegulatorReviewUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        HSETIApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload =
                (HSETIApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getRegulatorReviewAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
