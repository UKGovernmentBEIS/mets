package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmpEmissionsReductionClaimTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_exist_is_true_and_all_procedure_forms_are_null_then_invalid() {
        EmpEmissionsReductionClaim emissionsReductionClaim = EmpEmissionsReductionClaim.builder().exist(true).build();

        Set<ConstraintViolation<EmpEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertThat(violations).isNotEmpty();
        assertEquals(3, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder("{emp.emissionsReductionClaim.exist.safMonitoringSystemsAndProcesses}",
                "{emp.emissionsReductionClaim.exist.rtfoSustainabilityCriteria}",
                "{emp.emissionsReductionClaim.exist.safDuplicationPrevention}");
    }

    @Test
    void when_exist_is_true_and_at_least_one_procedure_form_is_null_then_invalid() {
        EmpEmissionsReductionClaim emissionsReductionClaim = EmpEmissionsReductionClaim.builder()
            .exist(true)
            .safMonitoringSystemsAndProcesses(createProcedureForm())
            .safDuplicationPrevention(createProcedureForm())
            .build();

        Set<ConstraintViolation<EmpEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertThat(violations).isNotEmpty();
        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder("{emp.emissionsReductionClaim.exist.rtfoSustainabilityCriteria}");
    }

    @Test
    void when_exist_is_true_and_all_procedure_forms_are_not_null_then_valid() {
        EmpEmissionsReductionClaim emissionsReductionClaim = EmpEmissionsReductionClaim.builder()
            .exist(true)
            .safMonitoringSystemsAndProcesses(createProcedureForm())
            .rtfoSustainabilityCriteria(createProcedureForm())
            .safDuplicationPrevention(createProcedureForm())
            .build();

        Set<ConstraintViolation<EmpEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertThat(violations).isEmpty();
    }

    @Test
    void when_exist_is_false_and_at_least_one_procedure_form_is_not_null_then_invalid() {
        EmpEmissionsReductionClaim emissionsReductionClaim = EmpEmissionsReductionClaim.builder()
            .exist(false)
            .safDuplicationPrevention(createProcedureForm())
            .build();

        Set<ConstraintViolation<EmpEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertThat(violations).isNotEmpty();
        assertEquals(1, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactly("{emp.emissionsReductionClaim.exist.safDuplicationPrevention}");

    }

    @Test
    void when_exist_is_false_and_all_procedure_forms_are_null_then_valid() {
        EmpEmissionsReductionClaim emissionsReductionClaim = EmpEmissionsReductionClaim.builder()
            .exist(false)
            .build();

        Set<ConstraintViolation<EmpEmissionsReductionClaim>> violations = validator.validate(emissionsReductionClaim);

        assertThat(violations).isEmpty();
    }

    private EmpProcedureForm createProcedureForm() {
        return EmpProcedureForm.builder()
            .procedureDescription("procedure description")
            .procedureDocumentName("procedure document name")
            .procedureReference("procedure reference")
            .responsibleDepartmentOrRole("responsible department")
            .locationOfRecords("location of records")
            .itSystemUsed("IT system")
            .build();
    }

}