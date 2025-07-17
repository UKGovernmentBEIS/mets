package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class PermitSurrenderAttachmentsValidator {
    
    private final FileAttachmentService fileAttachmentService;
    
    public boolean attachmentsExist(final Set<UUID> sectionAttachments) {
        Set<UUID> nonNullFiles =  sectionAttachments.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        if (nonNullFiles.isEmpty()) {
            return true;
        } 
        
        return fileAttachmentService.fileAttachmentsExist(nonNullFiles.stream().map(UUID::toString).collect(Collectors.toSet()));
    }
    
    public boolean sectionAttachmentsReferencedInPermitSurrender(final Set<UUID> sectionAttachments, final Set<UUID> permitSurrenderAttachments) {
        return permitSurrenderAttachments.stream().filter(Objects::nonNull).collect(Collectors.toSet()).containsAll(sectionAttachments);
    }

}
