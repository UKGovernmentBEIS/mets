package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TransferTest {
    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validTransferredModelWithAccountingForTransferAndEmitter() {
        TransferCO2 transfer = TransferCO2.builder()
            .entryAccountingForTransfer(true)
            .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
            .transferType(TransferType.TRANSFER_CO2)
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .installationEmitter(InstallationEmitter.builder()
                .emitterId("EM123")
                .email("test@test.com")
                .build())
            .build();

        Set<ConstraintViolation<TransferCO2>> violations = validator.validate(transfer);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void invalidTransferredModelWithoutTransferType() {
        TransferCO2 transfer = TransferCO2.builder()
            .entryAccountingForTransfer(true)
            .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .installationEmitter(InstallationEmitter.builder()
                .emitterId("EM123")
                .email("test@test.com")
                .build())
            .build();

        Set<ConstraintViolation<TransferCO2>> violations = validator.validate(transfer);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void invalidTransferredModelWithNoAccountingForTransferAndEmitter() {
        TransferCO2 transfer = TransferCO2.builder()
            .entryAccountingForTransfer(false)
            .transferType(TransferType.TRANSFER_CO2)
            .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .installationEmitter(InstallationEmitter.builder()
                .emitterId("EM123")
                .email("test@test.com")
                .build())
            .build();

        Set<ConstraintViolation<TransferCO2>> violations = validator.validate(transfer);

        assertThat(violations.size()).isEqualTo(3);
    }

    @Test
    public void invalidTransferredModelWithNoAccountingForTransferAndInstallationDetails() {
        TransferCO2 transfer = TransferCO2.builder()
            .entryAccountingForTransfer(false)
            .transferType(TransferType.TRANSFER_CO2)
            .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
            .installationDetailsType(InstallationDetailsType.INSTALLATION_DETAILS)
            .installationDetails(InstallationDetails.builder()
                .installationName("name")
                .line1("line1")
                .line2("line2")
                .city("city")
                .email("test@test.com")
                .postcode("postcode")
                .build())
            .build();

        Set<ConstraintViolation<Transfer>> violations = validator.validate(transfer);

        assertThat(violations.size()).isEqualTo(3);
    }

    @Test
    public void validTransferredModelWithAccountingForTransferAndInstallationName() {
        TransferCO2 transfer = TransferCO2.builder()
            .entryAccountingForTransfer(true)
            .transferType(TransferType.TRANSFER_CO2)
            .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
            .installationDetailsType(InstallationDetailsType.INSTALLATION_DETAILS)
            .installationDetails(InstallationDetails.builder()
                .installationName("installation")
                .line1("line1")
                .line2("line2")
                .city("city")
                .postcode("postcode")
                .email("test@test.com")
                .build())
            .build();

        Set<ConstraintViolation<Transfer>> violations = validator.validate(transfer);

        assertThat(violations.size()).isEqualTo(0);
    }
}
