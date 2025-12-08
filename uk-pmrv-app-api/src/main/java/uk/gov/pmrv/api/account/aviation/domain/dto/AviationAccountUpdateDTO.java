package uk.gov.pmrv.api.account.aviation.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AviationAccountUpdateDTO {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Min(0)
    @Max(9999999999L)
    private Long sopId;

    @NotBlank
    @Size(max = 255)
    private String crcoCode;

    @Valid
    private LocationOnShoreStateDTO location;
}
