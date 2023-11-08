package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PartnershipOrganisation extends OrganisationStructure {

    @Size(max = 255)
    private String partnershipName;

    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<@NotBlank @Size(max = 255) String> partners = new HashSet<>();
}
