package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class NerSubmitValidatorTest {

    @InjectMocks
    private NerSubmitValidator validator;

    @Mock
    private NerAttachmentsValidator attachmentsValidator;

    @Test
    void validateTaskPayload_whenAttachmentsMissing_thenException() {

        final UUID attachment = UUID.randomUUID();
        final NerApplicationSubmitRequestTaskPayload payload =
            NerApplicationSubmitRequestTaskPayload.builder()
                .additionalDocuments(AdditionalDocuments.builder().documents(Set.of(attachment)).build())
                .build();

        when(attachmentsValidator.attachmentsExist(Set.of(attachment))).thenReturn(false);

        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validateSubmitTaskPayload(payload));

        assertEquals(ErrorCode.INVALID_NER, businessException.getErrorCode());
    }

    @Test
    void validateTaskPayload_whenAttachmentsNotReferenced_thenException() {

        final UUID attachment = UUID.randomUUID();
        final NerApplicationSubmitRequestTaskPayload payload =
            NerApplicationSubmitRequestTaskPayload.builder()
                .additionalDocuments(AdditionalDocuments.builder().documents(Set.of(attachment)).build())
                .build();

        when(attachmentsValidator.attachmentsExist(any())).thenReturn(true);
        when(attachmentsValidator.attachmentsReferenced(any(), any())).thenReturn(false);

        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validateSubmitTaskPayload(payload));

        assertEquals(ErrorCode.INVALID_NER, businessException.getErrorCode());
    }
}
