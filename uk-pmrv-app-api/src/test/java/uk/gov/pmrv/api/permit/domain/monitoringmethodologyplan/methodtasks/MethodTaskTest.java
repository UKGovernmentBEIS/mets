package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType.ADIPIC_ACID;
import static uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType.AMMONIA;
import static uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType.PRIMARY_ALUMINIUM;

class MethodTaskTest {

    private final Validator validator;

    public MethodTaskTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void testValidWhenPhysicalPartsAndUnitsAnswerIsTrueAndConnectionsWithinLimits() {
        MethodTask task = new MethodTask();
        task.setPhysicalPartsAndUnitsAnswer(true);
        task.setAvoidDoubleCountToggle(false);
        task.setConnections(List.of(
                methodTaskConnection("1","N1", List.of(
                        PRIMARY_ALUMINIUM,
                        ADIPIC_ACID
                ))
        ));
        task.setAssignParts("Test");

        Set<ConstraintViolation<MethodTask>> violations = validator.validate(task);
        assertEquals(0, violations.size(), "There should be no validation errors");
    }

    @Test
    void testValidWhenPhysicalPartsAndUnitsAnswerIsTrueAndConnectionsWithinLimits_missingAssignPartsText() {
        MethodTask task = new MethodTask();
        task.setPhysicalPartsAndUnitsAnswer(true);
        task.setAvoidDoubleCountToggle(false);
        task.setConnections(List.of(
                methodTaskConnection("1","N1", List.of(
                        PRIMARY_ALUMINIUM,
                        ADIPIC_ACID
                ))
        ));

        Set<ConstraintViolation<MethodTask>> violations = validator.validate(task);
        assertEquals(1, violations.size(), "There should 1 validation errors," +
                "about missing assign part texts and not blank");
    }

    @Test
    void testInvalidWhenPhysicalPartsAndUnitsAnswerIsTrueAndConnectionsEmpty() {
        MethodTask task = new MethodTask();
        task.setPhysicalPartsAndUnitsAnswer(true);
        task.setAvoidDoubleCountToggle(false);
        task.setAssignParts("Test");
        task.setConnections(new ArrayList<>());

        Set<ConstraintViolation<MethodTask>> violations = validator.validate(task);
        assertEquals(1, violations.size(), "There should be a validation error for empty connections");
    }

    @Test
    void testValidWhenPhysicalPartsAndUnitsAnswerIsFalseAndConnectionsEmpty() {
        MethodTask task = new MethodTask();
        task.setPhysicalPartsAndUnitsAnswer(false);
        task.setAvoidDoubleCountToggle(false);
        task.setConnections(new ArrayList<>());

        Set<ConstraintViolation<MethodTask>> violations = validator.validate(task);
        assertEquals(0, violations.size(), "There should be no validation errors");
    }

    @Test
    void testInvalidWhenPhysicalPartsAndUnitsAnswerIsFalseAndConnectionsNotEmpty() {
        MethodTask task = new MethodTask();
        task.setPhysicalPartsAndUnitsAnswer(false);
        task.setAvoidDoubleCountToggle(false);
        task.setAssignParts("Test");
        task.setConnections(List.of(methodTaskConnection("1", "N1", List.of(ADIPIC_ACID, AMMONIA))));

        Set<ConstraintViolation<MethodTask>> violations = validator.validate(task);
        assertEquals(1, violations.size(), "There should be a validation error for non-empty connections");
    }


    private MethodTaskConnection methodTaskConnection(String itemId, String name, List<SubInstallationType> types) {
        return MethodTaskConnection.builder().itemId(itemId).itemName(name).subInstallations(types).build();
    }
}
