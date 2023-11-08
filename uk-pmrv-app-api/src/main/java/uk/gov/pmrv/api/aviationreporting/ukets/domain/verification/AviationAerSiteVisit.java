package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(schema = AviationAerInPersonSiteVisit.class, value = "IN_PERSON"),
        @DiscriminatorMapping(schema = AviationAerVirtualSiteVisit.class, value = "VIRTUAL"),
    },
    discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AviationAerInPersonSiteVisit.class, name = "IN_PERSON"),
    @JsonSubTypes.Type(value = AviationAerVirtualSiteVisit.class, name = "VIRTUAL"),
})

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerSiteVisit {

    @NotNull
    private AviationAerSiteVisitType type;
}
