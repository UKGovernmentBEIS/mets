package uk.gov.pmrv.api.migration.emp.ukets.consolidationnumber;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmpVersionRow {

    private Long accountId;

    private int consolidationNumber;
}
