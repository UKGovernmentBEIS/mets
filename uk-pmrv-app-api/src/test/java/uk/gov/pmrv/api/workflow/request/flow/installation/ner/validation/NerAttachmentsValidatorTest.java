package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

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

@ExtendWith(MockitoExtension.class)
class NerAttachmentsValidatorTest {

    @InjectMocks
    private NerAttachmentsValidator validator;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void attachmentsExist() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of(attachment1, attachment2);

        when(fileAttachmentService.fileAttachmentsExist(
            Set.of(attachment1.toString(), attachment2.toString()))).thenReturn(true);

        boolean result = validator.attachmentsExist(sectionAttachments);

        assertThat(result).isTrue();
        verify(fileAttachmentService, times(1)).fileAttachmentsExist(
            Set.of(attachment1.toString(), attachment2.toString()));
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
        final Set<UUID> nerAttachments = Set.of(attachment1, attachment2, UUID.randomUUID());

        boolean result =
            validator.attachmentsReferenced(sectionAttachments, nerAttachments);

        assertThat(result).isTrue();
    }

    @Test
    void sectionAttachmentsReferenced_not_exist() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of(attachment1, attachment2);
        final Set<UUID> nerAttachments = Set.of(attachment1, UUID.randomUUID());

        boolean result =
            validator.attachmentsReferenced(sectionAttachments, nerAttachments);

        assertThat(result).isFalse();
    }

    @Test
    void sectionAttachmentsReferenced_empty_section_attachments() {
        UUID attachment1 = UUID.randomUUID();
        final Set<UUID> sectionAttachments = Set.of();
        final Set<UUID> nerAttachments = Set.of(attachment1, UUID.randomUUID());

        boolean result =
            validator.attachmentsReferenced(sectionAttachments, nerAttachments);

        assertThat(result).isTrue();
    }
}
