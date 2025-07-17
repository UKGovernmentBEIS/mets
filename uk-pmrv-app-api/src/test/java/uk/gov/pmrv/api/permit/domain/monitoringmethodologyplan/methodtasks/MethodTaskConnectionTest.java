package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodTaskConnectionTest {

    private final Validator validator;

    public MethodTaskConnectionTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidMethodTaskConnection() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("123")
                .itemName("Valid Item Name")
                .subInstallations(List.of(SubInstallationType.AMMONIA, SubInstallationType.ADIPIC_ACID))
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(0, violations.size(), "There should be no validation errors for a valid MethodTaskConnection");
    }

    @Test
    void testInvalidWhenItemIdIsNull() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId(null) // Invalid
                .itemName("Valid Item Name")
                .subInstallations(List.of(SubInstallationType.AMMONIA, SubInstallationType.ADIPIC_ACID))
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for null itemId");
    }

    @Test
    void testInvalidWhenItemIdExceedsMaxLength() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("a".repeat(256)) // Invalid: exceeds max length
                .itemName("Valid Item Name")
                .subInstallations(List.of(SubInstallationType.AMMONIA, SubInstallationType.AROMATICS))
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for itemId exceeding max length");
    }

    @Test
    void testInvalidWhenItemNameIsBlank() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("123")
                .itemName("  ") // Invalid: blank string
                .subInstallations(List.of(SubInstallationType.AMMONIA, SubInstallationType.AROMATICS))
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for blank itemName");
    }

    @Test
    void testInvalidWhenItemNameExceedsMaxLength() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("123")
                .itemName("a".repeat(10001)) // Invalid: exceeds max length
                .subInstallations(List.of(SubInstallationType.AMMONIA, SubInstallationType.AROMATICS))
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for itemName exceeding max length");
    }

    @Test
    void testInvalidWhenSubInstallationsIsNull() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("123")
                .itemName("Valid Item Name")
                .subInstallations(new ArrayList<>()) // Invalid: empty list
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for minimum 2 subInstallations");
    }

    @Test
    void testInvalidWhenSubInstallationsIsEmpty() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("123")
                .itemName("Valid Item Name")
                .subInstallations(new ArrayList<>()) // Invalid: empty list
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for empty subInstallations");
    }

    @Test
    void testInvalidWhenSubInstallationsSizeLessThanMin() {
        MethodTaskConnection connection = MethodTaskConnection.builder()
                .itemId("123")
                .itemName("Valid Item Name")
                .subInstallations(List.of(SubInstallationType.ADIPIC_ACID)) // Invalid: size < 2
                .build();

        Set<ConstraintViolation<MethodTaskConnection>> violations = validator.validate(connection);
        assertEquals(1, violations.size(), "There should be a validation error for subInstallations with size less than 2");
    }
}
