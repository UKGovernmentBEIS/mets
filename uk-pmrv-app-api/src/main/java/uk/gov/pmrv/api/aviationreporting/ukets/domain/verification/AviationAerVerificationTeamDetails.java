package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerVerificationTeamDetails {

    @NotBlank
    @Size(max = 500)
    private String leadEtsAuditor;

    @NotBlank
    @Size(max = 10000)
    private String etsAuditors;

    @NotBlank
    @Size(max = 10000)
    private String etsTechnicalExperts;

    @NotBlank
    @Size(max = 500)
    private String independentReviewer;

    @NotBlank
    @Size(max = 10000)
    private String technicalExperts;

    @NotBlank
    @Size(max = 500)
    private String authorisedSignatoryName;
}
