package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = IndividualOrganisationDetails.class, value = "SOLE_TRADER"),
                @DiscriminatorMapping(schema = BusinessOrganisationDetails.class, value = "LIMITED_COMPANY"),
                @DiscriminatorMapping(schema = BusinessOrganisationDetails.class, value = "PARTNERSHIP"),
                @DiscriminatorMapping(schema = BusinessOrganisationDetails.class, value = "OTHER")
        },
        discriminatorProperty = "organisationLegalStatus")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "organisationLegalStatus",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = IndividualOrganisationDetails.class, name = "SOLE_TRADER"),
        @JsonSubTypes.Type(value = BusinessOrganisationDetails.class, name = "LIMITED_COMPANY"),
        @JsonSubTypes.Type(value = BusinessOrganisationDetails.class, name = "PARTNERSHIP"),
        @JsonSubTypes.Type(value = BusinessOrganisationDetails.class, name = "OTHER")
})
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class RegistryIntegrationOrganizationDetails {

    private LegalEntityType organisationLegalStatus;
}
