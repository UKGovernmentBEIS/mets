package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;


class AviationAerCorsiaAnnualOffsettingValidatorServiceTest {

    private static Validator validator;

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingValidatorService aviationAerCorsiaAnnualOffsettingValidatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void validateAviationAerCorsiaAnnualOffsetting_shouldPassValidation() {
        // Arrange
        Year year = Year.of(2023);

        AviationAerCorsiaAnnualOffsetting offsettingData = AviationAerCorsiaAnnualOffsetting.builder()
                .schemeYear(year)
                .sectorGrowth(2.34)
                .totalChapter(100)
                .calculatedAnnualOffsetting(2)  // 2.34 * 100 = 234
                .build();

        // Assert
        assertTrue(validator.validate(offsettingData).isEmpty());
        assertDoesNotThrow(() -> aviationAerCorsiaAnnualOffsettingValidatorService.validateAviationAerCorsiaAnnualOffsetting(offsettingData));
    }

    @Test
    void validateAviationAerCorsiaAnnualOffsetting_failedValidation() {
        // Arrange

        AviationAerCorsiaAnnualOffsetting offsettingData = AviationAerCorsiaAnnualOffsetting.builder()
            .schemeYear(null) // should not be null
            .sectorGrowth(2.345) // more fraction digits than allowed
            .totalChapter(-1) // should be positive
            .calculatedAnnualOffsetting(2)
            .build();

        // Act & Assert
        assertTrue(validator.validate(offsettingData).size() == 3);
    }


    @Test
    void validateAviationAerCorsiaAnnualOffsetting_failedCalculationValidation() {
        // Arrange
        Year year = Year.of(2024);

        AviationAerCorsiaAnnualOffsetting offsettingData = AviationAerCorsiaAnnualOffsetting.builder()
            .schemeYear(year)
            .sectorGrowth(2.34)
            .totalChapter(100)
            .calculatedAnnualOffsetting(9) // 2.34 * 100 = 234
            .build();

        // Act & Assert
        assertTrue(validator.validate(offsettingData).isEmpty());
        assertThrows(BusinessException.class, () -> aviationAerCorsiaAnnualOffsettingValidatorService.validateAviationAerCorsiaAnnualOffsetting(offsettingData));
    }
}