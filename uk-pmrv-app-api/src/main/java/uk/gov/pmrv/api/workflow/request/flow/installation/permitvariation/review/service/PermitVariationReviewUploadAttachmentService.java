package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadReviewGroupDecisionAttachment(Long requestTaskId, String attachmentUuid, String filename) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getReviewAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
