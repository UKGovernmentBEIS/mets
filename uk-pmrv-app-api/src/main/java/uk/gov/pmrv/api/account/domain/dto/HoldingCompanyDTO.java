package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The Holding Company DTO for Account.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HoldingCompanyDTO {

    @NotBlank(message = "{legalEntity.holdingCompany.name.notEmpty}")
    @Size(max = 256, message = "{legalEntity.holdingCompany.name.typeMismatch}")
    private String name;

    @Size(max = 50, message = "{legalEntity.holdingCompany.registrationNumber.typeMismatch}")
    private String registrationNumber;

    @NotNull
    @Valid
    private HoldingCompanyAddressDTO address;
}
