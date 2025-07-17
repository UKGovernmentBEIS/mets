package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#differentContactLocationExist) == (#differentContactLocation != null)}", message = "emp.operatorDetails.limitedCompany.differentContactLocation")
public class LimitedCompanyOrganisation extends OrganisationStructure {

    @NotBlank
    @Size(max = 40)
    private String registrationNumber;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> evidenceFiles = new HashSet<>();

    @NotNull
    private Boolean differentContactLocationExist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LocationOnShoreStateDTO differentContactLocation;

    @Override
    public Set<UUID> getAttachmentIds(){
        return evidenceFiles;
    }

}
