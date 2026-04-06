package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Validated
public class NERValidationService {

    private static final String NER_FILE_NAME_PATTERN = "^NER-\\d{5}-\\d+-v\\d+-(uploaded by (Operator|Regulator))-(.{1,10})\\.(?i)(doc|docx|xls|xlsx|ppt|pptx|vsd|vsdx|jpg|jpeg|pdf|png|tif|txt|dib|bmp|csv)$";
    private static final Pattern PATTERN = Pattern.compile(NER_FILE_NAME_PATTERN);

    public void validateNer(@Valid @NotNull NER ner) {
        // Validation is handled by JSR-303 annotations and SpEL expressions in NER class
    }

    public void validateNerFileName(@Valid @NotNull String nerFileName) {
        boolean isValid = false;

        if (StringUtil.isNotBlank(nerFileName))
            isValid = PATTERN.matcher(nerFileName).matches();

        if(!isValid) {
            throw new BusinessException(MetsErrorCode.NER_FILENAME_NOT_VALID);
        }
    }
}
