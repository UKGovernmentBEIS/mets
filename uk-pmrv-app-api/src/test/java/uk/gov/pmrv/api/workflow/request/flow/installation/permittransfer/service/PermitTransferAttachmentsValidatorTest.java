package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAttachmentsValidator;

@ExtendWith(MockitoExtension.class)
class PermitTransferAttachmentsValidatorTest {

    @InjectMocks
    private PermitTransferAttachmentsValidator validator;
    
    @Mock
    private FileAttachmentService fileAttachmentService;
    
    @Test
    void attachmentsExist() {
        
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of(attachment1, attachment2);
        
        when(fileAttachmentService.fileAttachmentsExist(Set.of(attachment1.toString(), attachment2.toString()))).thenReturn(true);
        
        boolean result = validator.attachmentsExist(sectionAttachments);
        
        assertThat(result).isTrue();
        verify(fileAttachmentService, times(1)).fileAttachmentsExist(Set.of(attachment1.toString(), attachment2.toString()));
    }
    
    @Test
    void attachmentsExist_empty() {
        
        boolean result = validator.attachmentsExist(Set.of());
        
        assertThat(result).isTrue();
        verify(fileAttachmentService, never()).fileAttachmentsExist(Mockito.anySet());
    }
    
    @Test
    void attachmentsExistInSections() {
        
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of(attachment1, attachment2);
        final Set<UUID> transferAttachments = Set.of(attachment1, attachment2, UUID.randomUUID());
        
        boolean result = validator.sectionAttachmentsReferenced(sectionAttachments, transferAttachments);
        
        assertThat(result).isTrue();
    }
    
    @Test
    void sectionAttachmentsReferencedInPermitTransfer_not_exist() {
        
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of(attachment1, attachment2);
        final Set<UUID> transferAttachments = Set.of(attachment1, UUID.randomUUID());
        
        boolean result = validator.sectionAttachmentsReferenced(sectionAttachments, transferAttachments);
        
        assertThat(result).isFalse();
    }
    
    @Test
    void sectionAttachmentsReferencedInPermitTransfer_empty_section_attachments() {
        
        UUID attachment1 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of();
        final Set<UUID> transferAttachments = Set.of(attachment1, UUID.randomUUID());
        
        boolean result = validator.sectionAttachmentsReferenced(sectionAttachments, transferAttachments);
        
        assertThat(result).isTrue();
    }
}
