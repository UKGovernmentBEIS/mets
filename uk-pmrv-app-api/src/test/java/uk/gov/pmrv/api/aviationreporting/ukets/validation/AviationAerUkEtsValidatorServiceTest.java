package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsValidatorServiceTest {

    private AviationAerUkEtsValidatorService aviationAerUkEtsValidatorService;

    @Spy
    private ArrayList<AviationAerUkEtsContextValidator> contextValidators;

    @Mock
    private AviationAerUkEtsOperatorDetailsSectionValidator operatorDetailsSectionValidator;

    @Mock
    private AviationAerUkEtsVerificationReportValidatorService ukEtsVerificationReportValidatorService;

    @BeforeEach
    void setUp() {
        contextValidators.add(operatorDetailsSectionValidator);
        aviationAerUkEtsValidatorService = new AviationAerUkEtsValidatorService(contextValidators, ukEtsVerificationReportValidatorService);
    }

    @Test
    void validateAer_when_emissions_report_required_valid() {
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .reportingRequired(Boolean.TRUE)
                .build();

        when(operatorDetailsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());

        aviationAerUkEtsValidatorService.validateAer(aerContainer);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
    }

    @Test
    void validateAer_when_emissions_report_required_invalid() {
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .reportingRequired(Boolean.TRUE)
                .build();

        when(operatorDetailsSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationOperatorDetails.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY))));

        BusinessException be = assertThrows(BusinessException.class,
                () -> aviationAerUkEtsValidatorService.validateAer(aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER);
    }

    @Test
    void validateAer_when_emissions_report_not_required_valid() {
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .reportingRequired(Boolean.FALSE)
                .build();

        aviationAerUkEtsValidatorService.validateAer(aerContainer);

        verifyNoInteractions(operatorDetailsSectionValidator);
    }

    @Test
    void validate_valid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .saf(AviationAerSaf.builder().exist(true).build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                .build())
            .build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        when(operatorDetailsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());

        aviationAerUkEtsValidatorService.validate(aerContainer);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
        verify(ukEtsVerificationReportValidatorService, times(1)).validate(verificationReport);
    }

    @Test
    void validate_invalid_when_aer_invalid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .saf(AviationAerSaf.builder().exist(true).build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        when(operatorDetailsSectionValidator.validate(aerContainer))
            .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationOperatorDetails.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY))));

        BusinessException be = assertThrows(BusinessException.class,
            () -> aviationAerUkEtsValidatorService.validate(aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
    }

    @Test
    void validate_invalid_when_verification_required_but_not_performed() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .saf(AviationAerSaf.builder().exist(true).build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .reportingRequired(true)
            .aer(aer)
            .build();

        when(operatorDetailsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());

        BusinessException be = assertThrows(BusinessException.class,
            () -> aviationAerUkEtsValidatorService.validate(aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
    }

    @Test
    void validate_invalid_when_verification_report_is_invalid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .saf(AviationAerSaf.builder().exist(true).build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                .build())
            .build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder().build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        when(operatorDetailsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        doThrow(new BusinessException(ErrorCode.INVALID_AVIATION_AER_VERIFICATION_REPORT))
            .when(ukEtsVerificationReportValidatorService).validate(verificationReport);

        BusinessException be = assertThrows(BusinessException.class,
            () -> aviationAerUkEtsValidatorService.validate(aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER_VERIFICATION_REPORT);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
        verify(ukEtsVerificationReportValidatorService, times(1)).validate(verificationReport);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, aviationAerUkEtsValidatorService.getEmissionTradingScheme());
    }

}