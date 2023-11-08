package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AdditionalDocumentsTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_additional_documents_exist_false_then_valid() {
        final EmpAdditionalDocuments additionalDocuments = EmpAdditionalDocuments.builder()
                .exist(false)
                .build();

        final Set<ConstraintViolation<EmpAdditionalDocuments>> violations = validator.validate(additionalDocuments);

        assertEquals(0, violations.size());
    }

    @Test
    void when_additional_documents_exist_true_then_valid() {
        final EmpAdditionalDocuments additionalDocuments = EmpAdditionalDocuments.builder()
                .exist(true)
                .documents(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<EmpAdditionalDocuments>> violations = validator.validate(additionalDocuments);

        assertEquals(0, violations.size());
    }

    @Test
    void when_additional_documents_exist_false_then_invalid() {
        final EmpAdditionalDocuments additionalDocuments = EmpAdditionalDocuments.builder()
                .exist(false)
                .documents(Set.of(UUID.randomUUID()))
                .build();

        final Set<ConstraintViolation<EmpAdditionalDocuments>> violations = validator.validate(additionalDocuments);

        assertEquals(1, violations.size());
    }

    @Test
    void when_additional_documents_exist_true_then_invalid() {
        final EmpAdditionalDocuments additionalDocuments = EmpAdditionalDocuments.builder()
                .exist(true)
                .build();

        final Set<ConstraintViolation<EmpAdditionalDocuments>> violations = validator.validate(additionalDocuments);

        assertEquals(1, violations.size());
    }
}
