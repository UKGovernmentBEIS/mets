package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.validation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonSubType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaEmissions;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFee;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaValidationServiceTest {

    @InjectMocks
    private AviationDoECorsiaValidationService validationService;

    @Test
    void validateAviationDoECorsia() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();


        assertDoesNotThrow(() -> validationService.validateAviationDoECorsia(doe));

    }

    @Test
    void validateAviationDoECorsia_determinationReasonCorrectionsToAVerifierReportAndEmptySubtypes_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }


    @Test
    void validateAviationDoECorsia_verifiedReportHasNotBeenSubmitted_missingOneOfThreeMeasurements_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_verifiedReportHasNotBeenSubmitted_offsettingEmissionsMoreThanInternationalEmissions_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(20))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(10))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(30))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_verifiedReportHasNotBeenSubmitted_measurementLessThanZero_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(-1))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(30))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_verifiedReportHasNotBeenSubmitted_measurementNotWholeNumber_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(20.42))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_verifiedReportHasNotBeenSubmitted_allCorrectMeasurements_doNotThrowBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                         .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(30))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();


        assertDoesNotThrow( () -> validationService.validateAviationDoECorsia(doe));
    }


    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_subtypesNotMatchingMeasurements_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(20))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_offsettingEmissionsMoreThanInternationalEmissions_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(20))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(10))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_measurementLessThanZero_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(-1))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_measurementNotWholeNumber_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(20.42))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_allInternationalFlightsZero_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(0))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(20))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_corsiaEligibleFuelsZero_doNotThrowBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(10))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(0))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        assertDoesNotThrow( () -> validationService.validateAviationDoECorsia(doe));
    }

    @Test
    void validateAviationDoECorsia_flightsWithOffsettingRequirementsZero_doNotThrowBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(0))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(10))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(10))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        assertDoesNotThrow( () -> validationService.validateAviationDoECorsia(doe));
    }

    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_onlyOneCorreectMeasurement_doNotThrowBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();


        assertDoesNotThrow( () -> validationService.validateAviationDoECorsia(doe));
    }

    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_allCorrectMeasurements_doNotThrowBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                         .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS,
                                 AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.valueOf(30))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();


        assertDoesNotThrow( () -> validationService.validateAviationDoECorsia(doe));
    }

    @Test
    void validateAviationDoECorsia_correctionsToAVerifiedReport_correctInternationalFlightsAndOffsettingMeasurements_doNotThrowBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                         .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS,
                                    AviationDoECorsiaDeterminationReasonSubType.CORRECTING_TOTAL_EMISSIONS_ON_ALL_INTERNATIONAL_FLIGHTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .emissionsAllInternationalFlights(BigDecimal.valueOf(20))
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();


        assertDoesNotThrow( () -> validationService.validateAviationDoECorsia(doe));
    }

    @Test
    void validateAviationDoECorsia_chargeOperator_feeDetailsNull_throwBusinessException() {
        AviationDoECorsia doe = AviationDoECorsia
                .builder()
                .determinationReason(AviationDoECorsiaDeterminationReason
                        .builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions
                        .builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.valueOf(10))
                        .build())
                .fee(AviationDoECorsiaFee
                        .builder()
                        .chargeOperator(true)
                        .feeDetails(null)
                        .build())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validationService.validateAviationDoECorsia(doe));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAviationDoECorsia_OnlyEligibleFuelEmissionsNotNull_otherEmissionsZero_shouldNotThrow() {
        AviationDoECorsia doe = AviationDoECorsia.builder()
                .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_RELATED_TO_A_CLAIM_FROM_CORSIA_ELIGIBLE_FUELS))
                        .build())
                .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ZERO)
                        .emissionsAllInternationalFlights(BigDecimal.ZERO)
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.ONE)
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        assertDoesNotThrow(() -> validationService.validateAviationDoECorsia(doe));
    }

    @Test
    void validateAviationDoECorsia_offsettingEmissionsBiggerThanInternational_offsettingEmissionsOne_internationalEmissionsZero_shouldNotThrow() {
        AviationDoECorsia doe = AviationDoECorsia.builder()
                .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                        .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                        .subtypes(Set.of(AviationDoECorsiaDeterminationReasonSubType.CORRECTING_EMISSIONS_ON_FLIGHTS_WITH_OFFSETTING_REQUIREMENTS))
                        .build())
                .emissions(AviationDoECorsiaEmissions.builder()
                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                        .emissionsAllInternationalFlights(BigDecimal.ZERO)
                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.ZERO)
                        .build())
                .fee(AviationDoECorsiaFee.builder().build())
                .build();

        assertDoesNotThrow(() -> validationService.validateAviationDoECorsia(doe));
    }
}
