package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#flightIdentificationType eq 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION') == (#icaoDesignators != null)}", message = "emp.operatorDetails.icaoDesignators")
@SpELExpression(expression = "{(#flightIdentificationType eq 'AIRCRAFT_REGISTRATION_MARKINGS') == (#aircraftRegistrationMarkings?.size() gt 0)}", message = "emp.operatorDetails.aircraftRegistrationMarkings")
public class FlightIdentification {

    @NotNull
    private FlightIdentificationType flightIdentificationType;

    @Size(max = 100)
    private String icaoDesignators;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotBlank @Size(max = 20) String> aircraftRegistrationMarkings = new HashSet<>();
}
