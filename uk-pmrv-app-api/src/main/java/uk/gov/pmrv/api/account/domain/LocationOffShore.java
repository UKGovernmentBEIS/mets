package uk.gov.pmrv.api.account.domain;


import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;

/**
 * The Location OffShore Entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@DiscriminatorValue(value = LocationType.Values.OFFSHORE)
public class LocationOffShore extends Location {

    /** The latitude. */
    @Column(name = "latitude")
    @NotBlank
    private String latitude;

    /** The longitude. */
    @Column(name = "longitude")
    @NotBlank
    private String longitude;
}
