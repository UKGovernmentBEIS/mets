package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsVerificationReportValidatorServiceTest {

    private AviationAerUkEtsVerificationReportValidatorService verificationReportValidatorService;

    @Spy
    private ArrayList<AviationAerUkEtsVerificationReportContextValidator> verificationReportContextValidators;

    @Mock
    private AviationAerUkEtsVerificationReportErcVerificationValidator ercVerificationValidator;

    @BeforeEach
    void setUp() {
        verificationReportContextValidators.add(ercVerificationValidator);
        verificationReportValidatorService = new AviationAerUkEtsVerificationReportValidatorService(verificationReportContextValidators);
    }

    @Test
    void validate_valid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();

        when(ercVerificationValidator.validate(verificationReport)).thenReturn(AviationAerValidationResult.validAviationAer());

        verificationReportValidatorService.validate(verificationReport);

        verify(ercVerificationValidator, times(1)).validate(verificationReport);
    }

    @Test
    void validate_invalid() {
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();

        when(ercVerificationValidator.validate(verificationReport))
            .thenReturn(AviationAerValidationResult.invalidAviationAer(Collections.emptyList()));

        BusinessException be = assertThrows(BusinessException.class,
            () -> verificationReportValidatorService.validate(verificationReport));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER_VERIFICATION_REPORT);

        verify(ercVerificationValidator, times(1)).validate(verificationReport);
    }
}