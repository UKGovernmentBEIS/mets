package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;

@Service
@RequiredArgsConstructor
public class PermitTransferAttachmentsValidator {

    private final FileAttachmentService fileAttachmentService;

    public boolean attachmentsExist(final Set<UUID> sectionAttachments) {
        if (sectionAttachments.isEmpty()) {
            return true;
        }
        return fileAttachmentService.fileAttachmentsExist(sectionAttachments.stream()
            .map(UUID::toString)
            .collect(Collectors.toSet()));
    }

    public boolean sectionAttachmentsReferenced(final Set<UUID> sectionAttachments, final Set<UUID> transferAttachments) {
        return transferAttachments.containsAll(sectionAttachments);
    }
}
