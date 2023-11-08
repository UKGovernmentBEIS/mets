package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = InPersonSiteVisit.class, value = "IN_PERSON"),
                @DiscriminatorMapping(schema = VirtualSiteVisit.class, value = "VIRTUAL"),
                @DiscriminatorMapping(schema = NoSiteVisit.class, value = "NO_VISIT")
        },
        discriminatorProperty = "siteVisitType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "siteVisitType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InPersonSiteVisit.class, name = "IN_PERSON"),
        @JsonSubTypes.Type(value = VirtualSiteVisit.class, name = "VIRTUAL"),
        @JsonSubTypes.Type(value = NoSiteVisit.class, name = "NO_VISIT")
})

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class SiteVisit {

    @NotNull
    private SiteVisitType siteVisitType;
}
