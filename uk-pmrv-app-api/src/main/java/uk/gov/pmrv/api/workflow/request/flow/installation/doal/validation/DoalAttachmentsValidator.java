package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoalAttachmentsValidator {

    private final FileAttachmentService fileAttachmentService;

    public boolean attachmentsExist(final Set<UUID> sectionAttachments) {
        Set<UUID> nonNullFiles =  sectionAttachments.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        if (nonNullFiles.isEmpty()) {
            return true;
        }
        return fileAttachmentService.fileAttachmentsExist(nonNullFiles.stream()
            .map(UUID::toString)
            .collect(Collectors.toSet()));
    }

    public boolean attachmentsReferenced(final Set<UUID> sectionAttachments, final Set<UUID> doalAttachments) {
        return doalAttachments.stream().filter(Objects::nonNull).collect(Collectors.toSet()).containsAll(sectionAttachments);
    }
}
