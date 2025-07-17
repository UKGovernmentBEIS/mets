package uk.gov.pmrv.api.aviationreporting.common.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "scheme", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AviationAerUkEtsContainer.class, name = "UK_ETS_AVIATION"),
    @JsonSubTypes.Type(value = AviationAerCorsiaContainer.class, name = "CORSIA")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#reportingRequired) == (#reportingObligationDetails == null)}", message = "aviationAer.reportingObligation.reportingObligationDetails")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#reportingRequired) == (#aer == null)}", message = "aviationAer.reportingObligation.reportingRequired")
public abstract class AviationAerContainer {

    @NotNull
    private EmissionTradingScheme scheme;

    @NotNull
    private Year reportingYear;
    
    @NotNull
    private Boolean reportingRequired;

    @Valid
    private AviationAerReportingObligationDetails reportingObligationDetails;

    @Valid
    @NotNull
    private ServiceContactDetails serviceContactDetails;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "scheme", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = AviationAerUkEtsTotalReportableEmissions.class, name = "UK_ETS_AVIATION"),
        @JsonSubTypes.Type(value = AviationAerCorsiaTotalReportableEmissions.class, name = "CORSIA"),
    })
    @Valid
    private AviationAerTotalReportableEmissions reportableEmissions;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "scheme", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = AviationAerUkEtsSubmittedEmissions.class, name = "UK_ETS_AVIATION"),
        @JsonSubTypes.Type(value = AviationAerCorsiaSubmittedEmissions.class, name = "CORSIA"),
    })
    @Valid
    private AviationAerSubmittedEmissions submittedEmissions;

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();
}
