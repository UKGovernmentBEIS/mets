package uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerDataGapsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_data_gaps_exist_then_valid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 10, BigDecimal.TEN)))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gaps_dont_exist_then_valid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gaps_dont_exist_datagaps_not_null_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.FALSE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 10, BigDecimal.valueOf(123.456))))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.dataGaps.exist}");
    }

    @Test
    void when_data_gaps_dont_exist_flights_percentage_not_null_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.FALSE)
                .affectedFlightsPercentage(BigDecimal.valueOf(1.5))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.dataGaps.affectedFlightsPercentage}");
    }

    @Test
    void when_data_gaps_exist_data_gaps_null_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .affectedFlightsPercentage(BigDecimal.valueOf(0.2))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.dataGaps.exist}");
    }

    @Test
    void when_data_gaps_exist_flights_percentage_null_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 10, BigDecimal.valueOf(123.45))))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.dataGaps.affectedFlightsPercentage}");
    }

    @Test
    void when_data_gaps_exist_flights_percentage_negative_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 10, BigDecimal.valueOf(23.4))))
                .affectedFlightsPercentage(BigDecimal.valueOf(-1))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("must be greater than or equal to 0");
    }

    @Test
    void when_data_gaps_exist_flights_percentage_more_decimals_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 10, BigDecimal.TEN)))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.45))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_blank_reason_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("", "type", "replacement method", 78, BigDecimal.valueOf(456.789))))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_blank_type_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "", "replacement method", 2, BigDecimal.valueOf(12.34))))
                .affectedFlightsPercentage(BigDecimal.valueOf(123))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_blank_replacement_method_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "", 125, BigDecimal.valueOf(34.5))))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_zero_flights_affected_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 0, BigDecimal.valueOf(34.5))))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_wrong_total_emissions_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 20, BigDecimal.valueOf(123.4567))))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_zero_total_emissions_then_invalid() {
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(List.of(createDataGap("reason", "type", "replacement method", 15, BigDecimal.ZERO)))
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_data_gaps_exist_null_data_gap_then_invalid() {
        List<AviationAerDataGap> dataGapList = new ArrayList<>();
        dataGapList.add(createDataGap("reason", "type", "replacement method", 20, BigDecimal.valueOf(34.5)));
        dataGapList.add(null);
        final AviationAerDataGaps dataGaps = AviationAerDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGaps(dataGapList)
                .affectedFlightsPercentage(BigDecimal.valueOf(123.4))
                .build();

        final Set<ConstraintViolation<AviationAerDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("must not be null");
    }

    private AviationAerDataGap createDataGap(String reason, String type, String replacementMethod, Integer flightsAffected, BigDecimal totalEmissions) {
        return AviationAerDataGap.builder()
                .reason(reason)
                .type(type)
                .replacementMethod(replacementMethod)
                .flightsAffected(flightsAffected)
                .totalEmissions(totalEmissions)
                .build();
    }
}
