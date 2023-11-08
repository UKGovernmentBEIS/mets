package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubsidiaryCompanyCorsia {

    @NotBlank
    private String operatorName;

    @NotNull
    @Valid
    private FlightIdentification flightIdentification;

    @NotNull
    @Valid
    private AirOperatingCertificateCorsia airOperatingCertificate;

    @NotBlank
    @Size(max = 40)
    private String companyRegistrationNumber;

    @NotNull
    @Valid
    private LocationOnShoreStateDTO registeredLocation;

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<FlightType> flightTypes = new HashSet<>();

    @NotBlank
    @Size(max = 10000)
    private String activityDescription;
}
