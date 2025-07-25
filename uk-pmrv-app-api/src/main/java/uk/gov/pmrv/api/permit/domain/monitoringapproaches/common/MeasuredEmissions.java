package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#samplingFrequency eq 'OTHER') == (#otherSamplingFrequency != null)}", 
    message = "permit.monitoringapproach.common.measuredemissions.otherSamplingFrequency")
public abstract class MeasuredEmissions {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> measurementDevicesOrMethods = new LinkedHashSet<>();

    @NotNull
    private MeasuredEmissionsSamplingFrequency samplingFrequency;

    @Size(max = 50)
    private String otherSamplingFrequency;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
}
