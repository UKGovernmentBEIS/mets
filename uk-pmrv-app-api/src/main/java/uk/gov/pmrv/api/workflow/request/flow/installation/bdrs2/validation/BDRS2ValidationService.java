package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Validated
public class BDRS2ValidationService {

    private static final String BDRS2_FILE_NAME_PATTERN = "^BDRS2-\\d{5}-(\\d{4})-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private static final Pattern PATTERN = Pattern.compile(BDRS2_FILE_NAME_PATTERN);

    public void validateBDRS2(@Valid @NotNull BDRS2 bdrs2) {
        // Validation is handled by JSR-303 annotations and SpEL expressions in BDRS2 class
    }

    public void validateVerificationReport(@Valid @NotNull BDRS2VerificationReport verificationReport) {
        // Validation is handled by JSR-303 annotations in BDRS2VerificationReport class
    }

    public void validateBDRS2FileName(@Valid @NotNull String bdrs2FileName) {
        boolean isValid = false;

        if (StringUtil.isNotBlank(bdrs2FileName))
            isValid = PATTERN.matcher(bdrs2FileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.BDRS2_FILENAME_NOT_VALID);
        }
    }
}
