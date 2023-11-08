package uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaDataGapsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_data_gaps_not_exist_valid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.FALSE)
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gaps_exist_less_than_5_per_cent_valid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.LESS_EQUAL_FIVE_PER_CENT)
                        .dataGapsPercentage(BigDecimal.valueOf(3.123))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gaps_exist_more_than_5_per_cent_valid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)
                        .dataGaps(List.of(AviationAerDataGap.builder()
                                                .reason("data gap reason")
                                                .type("data gap type")
                                                .replacementMethod("replacement method")
                                                .flightsAffected(15)
                                                .totalEmissions(BigDecimal.valueOf(12.345))
                                                .build(),
                                        AviationAerDataGap.builder()
                                                .reason("data gap reason 2")
                                                .type("data gap type 2")
                                                .replacementMethod("replacement method 2")
                                                .flightsAffected(20)
                                                .totalEmissions(BigDecimal.valueOf(23.45))
                                                .build()
                                )
                        )
                        .affectedFlightsPercentage(BigDecimal.valueOf(3.5))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(0, violations.size());
    }

    @Test
    void when_data_gaps_not_exist_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.FALSE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.LESS_EQUAL_FIVE_PER_CENT)
                        .dataGapsPercentage(BigDecimal.valueOf(3.123))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.dataGaps.exist}");
    }

    @Test
    void when_data_gaps_exist_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.dataGaps.exist}");
    }

    @Test
    void when_less_than_5_per_cent_percentage_missing_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.LESS_EQUAL_FIVE_PER_CENT)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.dataGaps.dataGapsPercentage}");
    }

    @Test
    void when_less_than_5_per_cent_data_gaps_exist_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.LESS_EQUAL_FIVE_PER_CENT)
                        .dataGapsPercentage(BigDecimal.valueOf(3.123))
                        .dataGaps(List.of(AviationAerDataGap.builder()
                                                .reason("data gap reason")
                                                .type("data gap type")
                                                .replacementMethod("replacement method")
                                                .flightsAffected(15)
                                                .totalEmissions(BigDecimal.valueOf(12.345))
                                                .build(),
                                        AviationAerDataGap.builder()
                                                .reason("data gap reason 2")
                                                .type("data gap type 2")
                                                .replacementMethod("replacement method 2")
                                                .flightsAffected(20)
                                                .totalEmissions(BigDecimal.valueOf(23.45))
                                                .build()
                                )
                        )
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.dataGaps.dataGaps}");
    }

    @Test
    void when_more_than_5_per_cent_percentage_exist_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)
                        .dataGapsPercentage(BigDecimal.valueOf(3.123))
                        .dataGaps(List.of(AviationAerDataGap.builder()
                                                .reason("data gap reason")
                                                .type("data gap type")
                                                .replacementMethod("replacement method")
                                                .flightsAffected(15)
                                                .totalEmissions(BigDecimal.valueOf(12.345))
                                                .build(),
                                        AviationAerDataGap.builder()
                                                .reason("data gap reason 2")
                                                .type("data gap type 2")
                                                .replacementMethod("replacement method 2")
                                                .flightsAffected(20)
                                                .totalEmissions(BigDecimal.valueOf(23.45))
                                                .build()
                                )
                        )
                        .affectedFlightsPercentage(BigDecimal.ONE)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.dataGaps.dataGapsPercentage}");
    }

    @Test
    void when_more_than_5_per_cent_data_gaps_missing_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)
                        .affectedFlightsPercentage(BigDecimal.valueOf(6.4))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.dataGaps.dataGaps}");
    }

    @Test
    void when_less_than_5_per_cent_percentage_exceeds_limits_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.LESS_EQUAL_FIVE_PER_CENT)
                        .dataGapsPercentage(BigDecimal.valueOf(6.234))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
    }

    @Test
    void when_more_than_5_per_cent_flights_percentage_missing_invalid() {

        final AviationAerCorsiaDataGaps dataGaps = AviationAerCorsiaDataGaps.builder()
                .exist(Boolean.TRUE)
                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                        .dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)
                        .dataGaps(List.of(AviationAerDataGap.builder()
                                                .reason("data gap reason")
                                                .type("data gap type")
                                                .replacementMethod("replacement method")
                                                .flightsAffected(15)
                                                .totalEmissions(BigDecimal.valueOf(12.345))
                                                .build(),
                                        AviationAerDataGap.builder()
                                                .reason("data gap reason 2")
                                                .type("data gap type 2")
                                                .replacementMethod("replacement method 2")
                                                .flightsAffected(20)
                                                .totalEmissions(BigDecimal.valueOf(23.45))
                                                .build()
                                )
                        )
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaDataGaps>> violations = validator.validate(dataGaps);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.dataGaps.affectedFlightsPercentage}");
    }
}
