package uk.gov.pmrv.api.account.installation.domain.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#registryReportingFirstYear le (T(java.time.Year).now().getValue() + 1)}",
        message = "The year cannot be bigger than one year from now")
public class AccountUpdateRegistryReportingFirstYearDTO {

    @Min(value = 2021, message = "The year must be the same as or after 2021")
    private Integer registryReportingFirstYear;

}
