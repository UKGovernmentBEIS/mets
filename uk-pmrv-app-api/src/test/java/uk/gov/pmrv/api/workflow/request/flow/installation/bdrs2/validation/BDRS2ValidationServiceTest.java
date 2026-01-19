package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

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
public class BDRS2ValidationServiceTest {

    @InjectMocks
    private BDRS2ValidationService bdrs2ValidationService;

    @Test
    public void validateBDRS2FileName_valid() {
        String[] validFileNames = {
                "BDRS2-00001-2025-v1-uploaded by Operator-OPp5.txt",
                "BDRS2-12345-2024-v2-uploaded by Regulator-Test.pdf",
                "BDRS2-45678-2023-v10-uploaded by Operator-Plant1.csv",
                "BDRS2-00099-2022-v99-uploaded by Regulator-Alpha.DOCX",
                "BDRS2-10001-2026-v5-uploaded by Operator-GH12.Jpg",
                "BDRS2-10001-2026-v5-uploaded by Operator-Inst#$.png" // symbols allowed (.{1,10})
        };

        for (String fileName : validFileNames) {
            assertDoesNotThrow(
                    () -> bdrs2ValidationService.validateBDRS2FileName(fileName),
                    "Expected no exception for valid filename: " + fileName
            );
        }
    }

    @Test
    public void validateBDRS2FileName_invalid() {
        String[] invalidFileNames = {
                "XYZBDRS2-00001-2025-v1-uploaded by Operator-OPp5.txt", // ❌ Doesn't start with BDRS2-
                "BDRS2-123-2025-v1-uploaded by Operator-OPp5.txt",     // ❌ Account ID < 5 digits
                "BDRS2-123456-2024-v2-uploaded by Regulator-Test.pdf", // ❌ Account ID > 5 digits
                "BDRS2-00001-2025-v-uploaded by Operator-OPp5.txt",    // ❌ Missing version number
                "BDRS2-00001-2025-v1-uploaded by User-OPp5.txt",       // ❌ Invalid uploader
                "BDRS2-00001-2025-v1-uploaded by Operator-LongFileName.txt", // ❌ >10 chars
                "BDRS2-00001-2025-v1-uploaded by Operator-OPp5",       // ❌ Missing extension
                "BDRS2-00001-2025-v1-uploaded by Operator-OPp5.ZIP",   // ❌ Extension not allowed
                "BDRS2-00001-2025-v1-uploaded by Operator-VERY_LONG_NAME.pdf" // ❌ Too long
        };

        for (String fileName : invalidFileNames) {
            BusinessException thrown = assertThrows(
                    BusinessException.class,
                    () -> bdrs2ValidationService.validateBDRS2FileName(fileName),
                    "Expected BusinessException for invalid filename: " + fileName
            );

            Assertions.assertEquals(
                    MetsErrorCode.BDRS2_FILENAME_NOT_VALID,
                    thrown.getErrorCode()
            );
        }
    }
}
