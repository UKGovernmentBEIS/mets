package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
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
class AviationAerCorsiaVerificationReportValidatorServiceTest {

    private AviationAerCorsiaVerificationReportValidatorService verificationReportValidatorService;

    @Spy
    private ArrayList<AviationAerCorsiaVerificationReportContextValidator> verificationReportContextValidators;

    @Mock
    private AviationAerCorsiaVerificationReportErcVerificationValidator ercVerificationValidator;

    @BeforeEach
    void setUp() {
        verificationReportContextValidators.add(ercVerificationValidator);
        verificationReportValidatorService = new AviationAerCorsiaVerificationReportValidatorService(verificationReportContextValidators);
    }

    @Test
    void validate_valid() {
        final AviationAerCorsia aerCorsia = AviationAerCorsia.builder().build();
        final AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder().build();

        when(ercVerificationValidator.validate(verificationReport, aerCorsia)).thenReturn(AviationAerValidationResult.validAviationAer());

        verificationReportValidatorService.validate(verificationReport, aerCorsia);

        verify(ercVerificationValidator, times(1)).validate(verificationReport, aerCorsia);
    }

    @Test
    void validate_invalid() {
        final AviationAerCorsia aerCorsia = AviationAerCorsia.builder().build();
        final AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder().build();

        when(ercVerificationValidator.validate(verificationReport, aerCorsia))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(Collections.emptyList()));

        BusinessException be = assertThrows(BusinessException.class,
                () -> verificationReportValidatorService.validate(verificationReport, aerCorsia));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER_VERIFICATION_REPORT);
        verify(ercVerificationValidator, times(1)).validate(verificationReport, aerCorsia);
    }
}
