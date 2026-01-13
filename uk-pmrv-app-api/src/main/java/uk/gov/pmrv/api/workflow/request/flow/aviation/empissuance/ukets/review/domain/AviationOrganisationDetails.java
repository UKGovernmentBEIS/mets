package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = AviationLimitedCompanyDetails.class, value = "LIMITED_COMPANY"),
                @DiscriminatorMapping(schema = AviationIndividualCompanyDetails.class, value = "INDIVIDUAL"),
                @DiscriminatorMapping(schema = AviationPartnershipDetails.class, value = "PARTNERSHIP"),
        },
        discriminatorProperty = "organisationLegalStatus")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "organisationLegalStatus",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AviationLimitedCompanyDetails.class, name = "LIMITED_COMPANY"),
        @JsonSubTypes.Type(value = AviationIndividualCompanyDetails.class, name = "INDIVIDUAL"),
        @JsonSubTypes.Type(value = AviationPartnershipDetails.class, name = "PARTNERSHIP"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AviationOrganisationDetails {

    private OrganisationLegalStatusType organisationLegalStatus;

}
