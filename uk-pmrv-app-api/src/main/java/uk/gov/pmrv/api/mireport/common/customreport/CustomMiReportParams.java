package uk.gov.pmrv.api.mireport.common.customreport;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;

import jakarta.validation.constraints.NotEmpty;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CustomMiReportParams extends MiReportParams {
    @NotEmpty
    private String sqlQuery;
}
