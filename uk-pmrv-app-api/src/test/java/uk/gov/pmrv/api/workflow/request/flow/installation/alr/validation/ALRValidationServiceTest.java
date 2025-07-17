package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ALRValidationServiceTest {

    @InjectMocks
    private ALRValidationService alrValidationService;

    @Test
    public void validateALRFileName_valid() {
        String[] validFileNames = {
                "ALR00017-2025-v1-uploaded by Operator-OPp5.txt",
                "ALR12345-2024-v2-uploaded by Regulator-Test.pdf",
                "ALR45678-2023-v10-uploaded by Operator-Plant1.csv",
                "ALR00099-2022-v99-uploaded by Regulator-Alpha.DOCX",
                "ALR10001-2026-v5-uploaded by Operator-GH12.Jpg",
                "ALR10001-2026-v5-uploaded by Operator-Inst#$.Jpg" // symbols allowed if using .{1,10}
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(() -> alrValidationService.validateALRFileName(fileName),
                    "Expected no exception for valid filename: " + fileName);
        }
    }

    @Test
    public void validateALRFileName_invalid() {
        String[] invalidFileNames = {
                "XYZALR00017-2025-v1-uploaded by Operator-OPp5.txt", // ❌ Doesn't start with ALR
                "ALR123-2025-v1-uploaded by Operator-OPp5.txt",       // ❌ Account ID < 5 digits
                "ALR123456-2024-v2-uploaded by Regulator-Test.Pdf",   // ❌ Account ID > 5 digits
                "ALR00017-2025-v-uploaded by Operator-OPp5.txt",      // ❌ Missing version number
                "ALR00017-2025-v1-uploaded by User-OPp5.txt",         // ❌ Invalid uploader
                "ALR00017-2025-v1-uploaded by Operator-LongFileName.txt", // ❌ Installation name > 10 characters
                "ALR00017-2025-v1-uploaded by Operator-OPp5",          // ❌ Missing file extension
                "ALR00017-2025-v1-uploaded by Operator-OPp5.ZIP",      // ❌ Invalid extension (not in whitelist)
                "ALR00017-2025-v1-uploaded by Operator-VERY_LONG_NAME.pdf" // ❌ Installation name too long
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(BusinessException.class,
                    () -> alrValidationService.validateALRFileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName);
            Assertions.assertEquals(MetsErrorCode.ALR_FILENAME_NOT_VALID, thrown.getErrorCode());
        }
    }
}
