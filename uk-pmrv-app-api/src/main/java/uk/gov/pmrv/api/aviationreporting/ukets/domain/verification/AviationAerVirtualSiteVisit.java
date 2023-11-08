package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerVirtualSiteVisit extends AviationAerSiteVisit {

    @NotBlank
    @Size(max = 10000)
    private String reason;
}
