package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestTaskAttachmentsUncoupleService {

    private final FileAttachmentService fileAttachmentService;
    private final RequestTaskService requestTaskService;

    @Transactional
    public void uncoupleAttachments(Long requestTaskId) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestTaskPayload requestTaskPayload = requestTask.getPayload();
        this.uncoupleAttachments(requestTaskPayload);
    }   
    
    @Transactional
    public void uncoupleAttachments(RequestTaskPayload requestTaskPayload) {

        if (requestTaskPayload == null) {
            return;
        }
        markReferencedAttachmentsAsSubmitted(requestTaskPayload);
        deleteUnreferencedAttachments(requestTaskPayload);
    }
    
    private void markReferencedAttachmentsAsSubmitted(RequestTaskPayload requestTaskPayload) {
        Set<UUID> referencedAttachmentIds = requestTaskPayload.getReferencedAttachmentIds().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        referencedAttachmentIds.forEach(attUuid ->
                fileAttachmentService.updateFileAttachmentStatus(attUuid.toString(), FileStatus.SUBMITTED));
    }
    
    private void deleteUnreferencedAttachments(RequestTaskPayload requestTaskPayload) {
        
        Set<UUID> allAttachments = requestTaskPayload.getAttachments().keySet();
        Set<UUID> referencedAttachments = requestTaskPayload.getReferencedAttachmentIds();
        
        Set<UUID> unreferencedAttachments = new HashSet<>(allAttachments);
        unreferencedAttachments.removeAll(referencedAttachments);

        final Set<UUID> deletedAttachments = new HashSet<>();
        unreferencedAttachments.forEach(attUuid -> {
            fileAttachmentService.deletePendingFileAttachment(attUuid.toString());
            deletedAttachments.add(attUuid);
        });
        requestTaskPayload.removeAttachments(deletedAttachments);
    }
    
    @Transactional
    public void deletePendingAttachments(final Long requestTaskId) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestTaskPayload requestTaskPayload = requestTask.getPayload();
        if (requestTaskPayload == null) {
            return;
        }
        final Set<UUID> deletedAttachments = new HashSet<>();
        requestTask.getPayload().getAttachmentsToDelete().keySet().forEach(attUuid -> {
            final boolean deleted = fileAttachmentService.deletePendingFileAttachment(attUuid.toString());
            if (deleted) {
                deletedAttachments.add(attUuid);
            }
        });
        requestTaskPayload.removeAttachments(deletedAttachments);
    }
}
