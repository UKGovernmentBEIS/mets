package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;

@Service
@RequiredArgsConstructor
public class NerAttachmentsValidator {

    private final FileAttachmentService fileAttachmentService;

    public boolean attachmentsExist(final Set<UUID> sectionAttachments) {
        if (sectionAttachments.isEmpty()) {
            return true;
        }
        return fileAttachmentService.fileAttachmentsExist(sectionAttachments.stream()
            .map(UUID::toString)
            .collect(Collectors.toSet()));
    }

    public boolean attachmentsReferenced(final Set<UUID> sectionAttachments, final Set<UUID> nerAttachments) {
        return nerAttachments.containsAll(sectionAttachments);
    }
}
