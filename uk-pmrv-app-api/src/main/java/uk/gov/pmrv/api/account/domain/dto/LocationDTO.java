package uk.gov.pmrv.api.account.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;

/**
 * The Location details DTO.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LocationOffShoreDTO.class, name = "OFFSHORE"),
        @JsonSubTypes.Type(value = LocationOnShoreDTO.class, name= "ONSHORE"),
        @JsonSubTypes.Type(value = LocationOnShoreStateDTO.class, name= "ONSHORE_STATE")
})
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class LocationDTO {

    /** The {@link LocationType}. */
    @NotNull(message = "{location.type.notEmpty}")
    private LocationType type;
}
