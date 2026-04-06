package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class NERValidationServiceTest {

    @InjectMocks
    private NERValidationService nerValidationService;

    @Test
    void validateNerFileName_valid() {
        String[] validFileNames = {
                "NER-00001-1-v1-uploaded by Operator-OPp5.txt",
                "NER-12345-2024-v2-uploaded by Regulator-Test.pdf",
                "NER-45678-2023-v10-uploaded by Operator-Plant1.csv",
                "NER-00099-2022-v99-uploaded by Regulator-Alpha.DOCX",
                "NER-10001-2026-v5-uploaded by Operator-GH12.Jpg",
                "NER-10001-2026-v5-uploaded by Operator-Inst#$.png" // symbols allowed
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(
                    () -> nerValidationService.validateNerFileName(fileName),
                    "Expected no exception for valid filename: " + fileName
            );
        }
    }

    @Test
    void validateNerFileName_invalid() {
        String[] invalidFileNames = {
                "XYZNER-00001-2025-v1-uploaded by Operator-OPp5.txt", // wrong prefix
                "NER-123-2025-v1-uploaded by Operator-OPp5.txt",     // < 5 digits
                "NER-123456-2024-v2-uploaded by Regulator-Test.pdf", // > 5 digits
                "NER-00001-2025-v-uploaded by Operator-OPp5.txt",    // missing version number
                "NER-00001-2025-v1-uploaded by User-OPp5.txt",       // invalid uploader
                "NER-00001-2025-v1-uploaded by Operator-LongFileName.txt", // >10 chars
                "NER-00001-2025-v1-uploaded by Operator-OPp5",       // no extension
                "NER-00001-2025-v1-uploaded by Operator-OPp5.ZIP",   // invalid extension
                "NER-00001-2025-v1-uploaded by Operator-VERY_LONG_NAME.pdf" // too long
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(
                    BusinessException.class,
                    () -> nerValidationService.validateNerFileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName
            );

            Assertions.assertEquals(
                    MetsErrorCode.NER_FILENAME_NOT_VALID,
                    thrown.getErrorCode()
            );
        }
    }

    @Test
    void validateNer_valid() {
        final NER ner = NER.builder()
                .nerFiles(
                        NERFiles.builder()
                                .file(UUID.randomUUID())
                                .build()
                )
                .notes("Some notes")
                .build();

        assertDoesNotThrow(
                () -> nerValidationService.validateNer(ner),
                "Expected no exception for valid NER"
        );
    }
}
