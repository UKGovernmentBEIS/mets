package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class InstallationInspectionUploadAttachmentService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final InstallationInspectionApplicationSubmitRequestTaskPayload requestTaskPayload =
                (InstallationInspectionApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getInspectionAttachments().put(UUID.fromString(attachmentUuid), filename);
    }
}
