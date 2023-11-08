package uk.gov.pmrv.api.account.aviation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#emissionTradingScheme eq 'UK_ETS_AVIATION') || (#emissionTradingScheme eq 'CORSIA')}",
    message = "account.emissionTradingScheme.invalid")
public class AviationAccountCreationDTO {

    @NotBlank(message = "{account.name.notEmpty}")
    @Size(max = 255, message = "{account.name.typeMismatch}")
    private String name;

    @NotNull(message = "{account.emissionTradingScheme.notEmpty}")
    private EmissionTradingScheme emissionTradingScheme;

    @Min(0)
    @Max(9999999999L)
    private Long sopId;

    @NotBlank(message = "{account.crco.notEmpty}")
    @Size(max = 255, message = "{account.crco.typeMismatch}")
    private String crcoCode;

    @NotNull(message = "{account.commencementDate.notEmpty}")
    private LocalDate commencementDate;
}
