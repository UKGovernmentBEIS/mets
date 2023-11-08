package uk.gov.pmrv.api.reporting.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AerContainer {

    @Valid
    @NotNull
    private Aer aer;

    @NotNull
    private Year reportingYear;

    @Builder.Default
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    @NotNull
    private BigDecimal reportableEmissions = BigDecimal.ZERO;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    @Valid
    @NotNull
    private PermitOriginatedData permitOriginatedData;

    @Valid
    private AerVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();
}
