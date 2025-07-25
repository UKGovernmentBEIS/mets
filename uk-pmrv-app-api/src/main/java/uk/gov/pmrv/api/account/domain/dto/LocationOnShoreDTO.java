package uk.gov.pmrv.api.account.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.validation.GridReference;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

/**
 * The Location OnShore DTO type of address.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LocationOnShoreDTO extends LocationDTO {

    /** The UK Ordnance survey grid reference. */
    @NotBlank(message = "{locationOnShoreDTO.gridReference.notEmpty}")
    @GridReference(message = "{locationOnShoreDTO.gridReference.typeMismatch}")
    private String gridReference;

    /** The {@link AddressDTO}. */
    @NotNull(message = "{locationOnShoreDTO.address.notEmpty}")
    @Valid
    private AddressDTO address;
}
