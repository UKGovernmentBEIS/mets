package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AviationAerComplianceMonitoringReportingRulesTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_all_rules_are_met_valid() {
        AviationAerComplianceMonitoringReportingRules complianceMonitoringReportingRules =
            AviationAerComplianceMonitoringReportingRules.builder()
                .accuracyCompliant(Boolean.TRUE)
                .completenessCompliant(Boolean.TRUE)
                .consistencyCompliant(Boolean.TRUE)
                .comparabilityCompliant(Boolean.TRUE)
                .transparencyCompliant(Boolean.TRUE)
                .integrityCompliant(Boolean.TRUE)
                .build();

        Set<ConstraintViolation<AviationAerComplianceMonitoringReportingRules>> violations = validator.validate(complianceMonitoringReportingRules);

        assertEquals(0, violations.size());
    }

    @Test
    void when_all_rules_are_not_met_valid() {
        AviationAerComplianceMonitoringReportingRules complianceMonitoringReportingRules =
            AviationAerComplianceMonitoringReportingRules.builder()
                .accuracyCompliant(Boolean.FALSE)
                .accuracyNonCompliantReason("accuracy reason")
                .completenessCompliant(Boolean.FALSE)
                .completenessNonCompliantReason("completeness reason")
                .consistencyCompliant(Boolean.FALSE)
                .consistencyNonCompliantReason("consistency reason")
                .comparabilityCompliant(Boolean.FALSE)
                .comparabilityNonCompliantReason("comparability reason")
                .transparencyCompliant(Boolean.FALSE)
                .transparencyNonCompliantReason("transparency reason")
                .integrityCompliant(Boolean.FALSE)
                .integrityNonCompliantReason("integrity reason")
            .build();

        Set<ConstraintViolation<AviationAerComplianceMonitoringReportingRules>> violations = validator.validate(complianceMonitoringReportingRules);

        assertEquals(0, violations.size());
    }

    @Test
    void when_all_rules_are_not_met_and_no_reasons_provided_invalid() {
        AviationAerComplianceMonitoringReportingRules complianceMonitoringReportingRules =
            AviationAerComplianceMonitoringReportingRules.builder()
                .accuracyCompliant(Boolean.FALSE)
                .completenessCompliant(Boolean.FALSE)
                .consistencyCompliant(Boolean.FALSE)
                .comparabilityCompliant(Boolean.FALSE)
                .transparencyCompliant(Boolean.FALSE)
                .integrityCompliant(Boolean.FALSE)
                .build();

        Set<ConstraintViolation<AviationAerComplianceMonitoringReportingRules>> violations = validator.validate(complianceMonitoringReportingRules);

        assertEquals(6, violations.size());
    }

    @Test
    void when_all_rules_are_met_but_reasons_provided_invalid() {
        AviationAerComplianceMonitoringReportingRules complianceMonitoringReportingRules =
            AviationAerComplianceMonitoringReportingRules.builder()
                .accuracyCompliant(Boolean.TRUE)
                .accuracyNonCompliantReason("accuracy reason")
                .completenessCompliant(Boolean.TRUE)
                .completenessNonCompliantReason("completeness reason")
                .consistencyCompliant(Boolean.TRUE)
                .consistencyNonCompliantReason("consistency reason")
                .comparabilityCompliant(Boolean.TRUE)
                .comparabilityNonCompliantReason("comparability reason")
                .transparencyCompliant(Boolean.TRUE)
                .transparencyNonCompliantReason("transparency reason")
                .integrityCompliant(Boolean.TRUE)
                .integrityNonCompliantReason("integrity reason")
                .build();

        Set<ConstraintViolation<AviationAerComplianceMonitoringReportingRules>> violations = validator.validate(complianceMonitoringReportingRules);

        assertEquals(6, violations.size());
    }

}