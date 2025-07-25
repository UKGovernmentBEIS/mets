package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BDRRegulatorReviewUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        BDRApplicationRegulatorReviewSubmitRequestTaskPayload requestTaskPayload =
                (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getRegulatorReviewAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
