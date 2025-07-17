package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;

import java.util.regex.Pattern;

@Service
public class ALRValidationService {
    private final String ALR_FILE_NAME_PATTERN = "^ALR\\d{5}-\\d{4}-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private final Pattern PATTERN = Pattern.compile(ALR_FILE_NAME_PATTERN);

    public void validateALR(@Valid @NotNull ALR alr) {}

    public void validateVerificationReport(@Valid @NotNull ALRVerificationReport verificationReport) {}

    public void validateALRFileName(@Valid @NotNull String alrFileName) {
        boolean isValid = false;

        if (StringUtil.isNotBlank(alrFileName))
            isValid = PATTERN.matcher(alrFileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.ALR_FILENAME_NOT_VALID);
        }
    }
}
