package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileStatusInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
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
        // Set of UUIDs
        Set<String> uuids = requestTaskPayload.getReferencedAttachmentIds().stream()
                .filter(Objects::nonNull).map(UUID::toString).collect(Collectors.toSet());
        fileAttachmentService.updateFileAttachmentsStatus(uuids, FileStatus.SUBMITTED);
    }
    
    private void deleteUnreferencedAttachments(RequestTaskPayload requestTaskPayload) {
        
        Set<UUID> allAttachments = requestTaskPayload.getAttachments().keySet();
        Set<UUID> referencedAttachments = requestTaskPayload.getReferencedAttachmentIds();
        
        Set<UUID> unreferencedAttachments = new HashSet<>(allAttachments);
        unreferencedAttachments.removeAll(referencedAttachments);

        requestTaskPayload.removeAttachments(unreferencedAttachments);

        Set<String> attachmentsToBeDeleted = unreferencedAttachments.stream().map(UUID::toString).collect(Collectors.toSet());
        CompletableFuture.runAsync(() -> fileAttachmentService.deletePendingFileAttachments(attachmentsToBeDeleted))
                .exceptionally(ex -> {
                    log.error(ex);
                    return null;
                });
    }
    
    @Transactional
    public void deletePendingAttachments(final Long requestTaskId) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestTaskPayload requestTaskPayload = requestTask.getPayload();
        if (requestTaskPayload == null) {
            return;
        }

        Set<String> attachmentsUuids = requestTask.getPayload().getAttachmentsToDelete().keySet().stream()
                .map(UUID::toString).collect(Collectors.toSet());

        List<FileStatusInfoDTO> persistedAttachments = fileAttachmentService.getFilesStatus(attachmentsUuids);

        Set<String> persistedAttachmentsUuids = persistedAttachments.stream()
                .map(FileStatusInfoDTO::getUuid)
                .collect(Collectors.toSet());

        Set<String> pendingAttachmentsUuids = persistedAttachments.stream()
                .filter(att -> att.getStatus().equals(FileStatus.PENDING))
                .map(FileStatusInfoDTO::getUuid)
                .collect(Collectors.toSet());

        // Both the pending and the missing ones should be removed from payload
        Set<UUID> toBeRemovedFromPayload = Stream.concat(
                        pendingAttachmentsUuids.stream(),
                        attachmentsUuids.stream().filter(uuid -> !persistedAttachmentsUuids.contains(uuid))
                )
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        requestTaskPayload.removeAttachments(toBeRemovedFromPayload);

        CompletableFuture.runAsync(() -> fileAttachmentService.deleteFileAttachments(pendingAttachmentsUuids))
                .exceptionally(ex -> {
                    log.error(ex);
                    return null;
                });
    }
}
