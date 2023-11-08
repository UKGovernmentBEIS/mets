package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = LimitedCompanyOrganisation.class, value = "LIMITED_COMPANY"),
                @DiscriminatorMapping(schema = IndividualOrganisation.class, value = "INDIVIDUAL"),
                @DiscriminatorMapping(schema = PartnershipOrganisation.class, value = "PARTNERSHIP")
        },
        discriminatorProperty = "legalStatusType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "legalStatusType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LimitedCompanyOrganisation.class, name = "LIMITED_COMPANY"),
        @JsonSubTypes.Type(value = IndividualOrganisation.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = PartnershipOrganisation.class, name = "PARTNERSHIP")
})
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class OrganisationStructure {

    @NotNull
    private OrganisationLegalStatusType legalStatusType;

    @NotNull
    @Valid
    private LocationOnShoreStateDTO organisationLocation;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(new HashSet<>());
    }

}
