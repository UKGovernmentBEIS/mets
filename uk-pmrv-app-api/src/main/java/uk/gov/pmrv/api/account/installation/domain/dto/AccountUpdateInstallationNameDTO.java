package uk.gov.pmrv.api.account.installation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountUpdateInstallationNameDTO {

    @NotBlank
    @Size(max = 255)
    private String installationName;
}
