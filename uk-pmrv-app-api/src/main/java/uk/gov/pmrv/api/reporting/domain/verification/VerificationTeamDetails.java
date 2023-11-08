package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationTeamDetails {

    @NotBlank
    private String leadEtsAuditor;

    @NotBlank
    private String etsAuditors;

    @NotBlank
    private String etsTechnicalExperts;

    @NotBlank
    private String independentReviewer;

    @NotBlank
    private String technicalExperts;

    @NotBlank
    private String authorisedSignatoryName;
}
