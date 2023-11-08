package uk.gov.pmrv.api.reporting.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation.AerCalculationEmissionsValidator;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2.AerMeasurementCO2EmissionsValidator;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerValidatorServiceTest {


    private AerValidatorService aerValidatorService;
    @Mock
    private AerCalculationEmissionsValidator aerCalculationEmissionsValidator;

    @Mock
    private AerMeasurementCO2EmissionsValidator aerMeasurementCO2EmissionsValidator;

    @Mock
    private AerVerificationReportExistenceValidator aerVerificationReportExistenceValidator;

    @Mock
    private AerVerificationReportNotReportedChangesValidator aerVerificationReportNotReportedChangesValidator;

    @Mock
    private AerVerificationReportApprovedChangesValidator aerVerificationReportApprovedChangesValidator;

    @Mock
    private AerVerificationReportUncorrectedMisstatementsValidator aerVerificationReportUncorrectedMisstatementsValidator;

    @Mock
    private AerVerificationReportRecommendedImprovementsValidator aerVerificationReportRecommendedImprovementsValidator;

    @Mock
    private AerVerificationReportUncorrectedNonConformitiesValidator aerVerificationReportUncorrectedNonConformitiesValidator;

    @Mock
    private AerVerificationReportPriorYearIssuesValidator aerVerificationReportPriorYearIssuesValidator;

    @Mock
    private AerVerificationReportUncorrectedNonCompliancesValidator aerVerificationReportUncorrectedNonCompliancesValidator;

    @Spy
    private ArrayList<AerContextValidator> aerContextValidators;

    @Spy
    private ArrayList<AerVerificationReportContextValidator> aerVerificationReportContextValidators;


    @BeforeEach
    void setUp() {
        aerContextValidators.add(aerCalculationEmissionsValidator);
        aerContextValidators.add(aerMeasurementCO2EmissionsValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportNotReportedChangesValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportApprovedChangesValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportUncorrectedMisstatementsValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportRecommendedImprovementsValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportUncorrectedNonConformitiesValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportPriorYearIssuesValidator);
        aerVerificationReportContextValidators.add(aerVerificationReportUncorrectedNonCompliancesValidator);
        aerValidatorService = new AerValidatorService(aerContextValidators, aerVerificationReportExistenceValidator,
            aerVerificationReportContextValidators);
    }

    @Test
    void validateAer() {
        AerContainer aerContainer = AerContainer.builder().build();

        when(aerCalculationEmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.validAer());

        when(aerMeasurementCO2EmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.validAer());

        aerValidatorService.validateAer(aerContainer);

        verify(aerCalculationEmissionsValidator, times(1))
            .validate(aerContainer);

        verify(aerMeasurementCO2EmissionsValidator, times(1))
            .validate(aerContainer);
    }

    @Test
    void validateAer_invalid() {
        AerContainer aerContainer = AerContainer.builder().build();
        List<AerViolation> aerViolations = List.of(
            new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_MONITORING_TIER)
        );

        when(aerCalculationEmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));

        when(aerMeasurementCO2EmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));

        BusinessException be = assertThrows(BusinessException.class,
            () -> aerValidatorService.validateAer(aerContainer));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AER);

        verify(aerCalculationEmissionsValidator, times(1))
            .validate(aerContainer);

        verify(aerMeasurementCO2EmissionsValidator, times(1))
            .validate(aerContainer);
    }

    @Test
    void validate() {
        Long accountId = 1L;
        AerContainer aerContainer = AerContainer.builder()
            .permitOriginatedData(PermitOriginatedData.builder().build())
            .verificationReport(AerVerificationReport.builder()
                .build())
            .aer(Aer.builder().build())
            .build();

        when(aerCalculationEmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportUncorrectedMisstatementsValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportNotReportedChangesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportApprovedChangesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportRecommendedImprovementsValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportUncorrectedNonConformitiesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportPriorYearIssuesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportUncorrectedNonCompliancesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.validAer());
        when(aerMeasurementCO2EmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.validAer());

        aerValidatorService.validate(aerContainer, accountId);

        verify(aerCalculationEmissionsValidator, times(1))
            .validate(aerContainer);

        verify(aerVerificationReportExistenceValidator, times(1))
            .validate(aerContainer, accountId);

        verify(aerVerificationReportUncorrectedMisstatementsValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportNotReportedChangesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportApprovedChangesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportRecommendedImprovementsValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportUncorrectedNonConformitiesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportPriorYearIssuesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportUncorrectedNonCompliancesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerMeasurementCO2EmissionsValidator, times(1))
            .validate(aerContainer);

    }

    @Test
    void validate_invalid() {
        Long accountId = 1L;
        AerContainer aerContainer = AerContainer.builder()
            .permitOriginatedData(PermitOriginatedData.builder().build())
            .verificationReport(AerVerificationReport.builder()
                .build())
            .build();
        List<AerViolation> aerViolations = List.of(
            new AerViolation(AerVerificationReport.class.getSimpleName(),
                AerViolation.AerViolationMessage.VERIFICATION_INVALID_APPROVED_CHANGE_REFERENCE));

        when(aerCalculationEmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.validAer());
        when(aerVerificationReportUncorrectedMisstatementsValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerVerificationReportNotReportedChangesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerVerificationReportApprovedChangesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerVerificationReportRecommendedImprovementsValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerVerificationReportUncorrectedNonConformitiesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerVerificationReportPriorYearIssuesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerVerificationReportUncorrectedNonCompliancesValidator.validate(aerContainer.getVerificationReport(),
            aerContainer.getPermitOriginatedData()))
            .thenReturn(AerValidationResult.invalidAer(aerViolations));
        when(aerMeasurementCO2EmissionsValidator.validate(aerContainer))
            .thenReturn(AerValidationResult.validAer());

        BusinessException be = assertThrows(BusinessException.class,
            () -> aerValidatorService.validate(aerContainer, accountId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AER_VERIFICATION_REPORT);

        verify(aerCalculationEmissionsValidator, times(1))
            .validate(aerContainer);

        verify(aerVerificationReportExistenceValidator, times(1))
            .validate(aerContainer, accountId);

        verify(aerVerificationReportUncorrectedMisstatementsValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportNotReportedChangesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportApprovedChangesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportRecommendedImprovementsValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportUncorrectedNonConformitiesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportPriorYearIssuesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerVerificationReportUncorrectedNonCompliancesValidator, times(1))
            .validate(aerContainer.getVerificationReport(), aerContainer.getPermitOriginatedData());

        verify(aerMeasurementCO2EmissionsValidator, times(1))
            .validate(aerContainer);
    }
}