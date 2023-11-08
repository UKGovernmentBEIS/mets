package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.aircraftdata.AviationAerAircraftData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.confidentiality.AviationAerCorsiaConfidentiality;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGaps;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaValidatorServiceTest {

    private AviationAerCorsiaValidatorService aviationAerCorsiaValidatorService;

    @Spy
    private ArrayList<AviationAerCorsiaContextValidator> contextValidators;

    @Mock
    private AviationAerCorsiaOperatorDetailsSectionValidator operatorDetailsSectionValidator;

    @Mock
    private AviationAerCorsiaEmissionsMonitoringApproachSectionValidator monitoringApproachSectionValidator;

    @Mock
    private AviationAerCorsiaDataGapsSectionValidator dataGapsSectionValidator;

    @Mock
    private AviationAerCorsiaConfidentialitySectionValidator confidentialitySectionValidator;

    @Mock
    private AviationAerCorsiaAircraftDataSectionValidator aviationAerCorsiaAircraftDataSectionValidator;

    @Mock
    private AviationAerCorsiaVerificationReportValidatorService corsiaVerificationReportValidatorService;

    @BeforeEach
    void setUp() {
        contextValidators.add(operatorDetailsSectionValidator);
        contextValidators.add(monitoringApproachSectionValidator);
        contextValidators.add(dataGapsSectionValidator);
        contextValidators.add(confidentialitySectionValidator);
        contextValidators.add(aviationAerCorsiaAircraftDataSectionValidator);
        aviationAerCorsiaValidatorService = new AviationAerCorsiaValidatorService(contextValidators, corsiaVerificationReportValidatorService);
    }

    @Test
    void validateAer_when_emissions_report_required_valid() {
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingRequired(Boolean.TRUE)
                .build();

        when(operatorDetailsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(dataGapsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(monitoringApproachSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(confidentialitySectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(aviationAerCorsiaAircraftDataSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());

        aviationAerCorsiaValidatorService.validateAer(aerContainer);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
        verify(monitoringApproachSectionValidator, times(1)).validate(aerContainer);
        verify(dataGapsSectionValidator, times(1)).validate(aerContainer);
        verify(confidentialitySectionValidator, times(1)).validate(aerContainer);
        verify(aviationAerCorsiaAircraftDataSectionValidator, times(1)).validate(aerContainer);

    }

    @Test
    void validateAer_when_emissions_report_required_invalid() {
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingRequired(Boolean.TRUE)
                .build();

        when(operatorDetailsSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationCorsiaOperatorDetails.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY))));

        when(monitoringApproachSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerCorsiaMonitoringApproach.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_FLIGHT_TYPE_AND_FUEL_USE_COMBINATION))));
        when(dataGapsSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerCorsiaDataGaps.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_AFFECTED_FLIGHTS_PERCENTAGE))));
        when(confidentialitySectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerCorsiaConfidentiality.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_DOCUMENTS))));
        when(aviationAerCorsiaAircraftDataSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerAircraftData.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_START_DATE_END_DATE_SCHEME_YEAR))));
        BusinessException be = assertThrows(BusinessException.class,
                () -> aviationAerCorsiaValidatorService.validateAer(aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER);
    }

    @Test
    void validateAer_when_emissions_report_not_required_valid() {
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingRequired(Boolean.FALSE)
                .build();

        aviationAerCorsiaValidatorService.validateAer(aerContainer);

        verifyNoInteractions(operatorDetailsSectionValidator);
        verifyNoInteractions(monitoringApproachSectionValidator);
        verifyNoInteractions(dataGapsSectionValidator);
        verifyNoInteractions(confidentialitySectionValidator);
        verifyNoInteractions(aviationAerCorsiaAircraftDataSectionValidator);

    }

    @Test
    void validate_valid() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(AviationCorsiaOperatorDetails.builder().build())
            .build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder().build();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        when(operatorDetailsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(monitoringApproachSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(dataGapsSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(confidentialitySectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());
        when(aviationAerCorsiaAircraftDataSectionValidator.validate(aerContainer)).thenReturn(AviationAerValidationResult.validAviationAer());

        aviationAerCorsiaValidatorService.validate(aerContainer);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
        verify(monitoringApproachSectionValidator, times(1)).validate(aerContainer);
        verify(dataGapsSectionValidator, times(1)).validate(aerContainer);
        verify(confidentialitySectionValidator, times(1)).validate(aerContainer);
        verify(aviationAerCorsiaAircraftDataSectionValidator, times(1)).validate(aerContainer);
        verify(corsiaVerificationReportValidatorService, times(1)).validate(verificationReport, aer);
    }

    @Test
    void validate_invalid_when_aer_invalid() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(AviationCorsiaOperatorDetails.builder().build())
            .build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder().build();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
            .reportingRequired(true)
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        when(operatorDetailsSectionValidator.validate(aerContainer))
            .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationCorsiaOperatorDetails.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_ISSUING_AUTHORITY))));
        when(monitoringApproachSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerCorsiaMonitoringApproach.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_FLIGHT_TYPE_AND_FUEL_USE_COMBINATION))));
        when(dataGapsSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerCorsiaDataGaps.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_AFFECTED_FLIGHTS_PERCENTAGE))));
        when(confidentialitySectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerCorsiaConfidentiality.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_TOTAL_EMISSIONS_DOCUMENTS))));
        when(aviationAerCorsiaAircraftDataSectionValidator.validate(aerContainer))
                .thenReturn(AviationAerValidationResult.invalidAviationAer(List.of(new AviationAerViolation(AviationAerAircraftData.class.getSimpleName(), AviationAerViolation.AviationAerViolationMessage.INVALID_START_DATE_END_DATE_SCHEME_YEAR))));
        BusinessException be = assertThrows(BusinessException.class,
            () -> aviationAerCorsiaValidatorService.validate(aerContainer));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AVIATION_AER);

        verify(operatorDetailsSectionValidator, times(1)).validate(aerContainer);
        verify(monitoringApproachSectionValidator, times(1)).validate(aerContainer);
        verify(dataGapsSectionValidator, times(1)).validate(aerContainer);
        verify(confidentialitySectionValidator, times(1)).validate(aerContainer);
        verify(aviationAerCorsiaAircraftDataSectionValidator, times(1)).validate(aerContainer);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.CORSIA, aviationAerCorsiaValidatorService.getEmissionTradingScheme());
    }

}