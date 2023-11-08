package uk.gov.pmrv.api.migration.permit.consolidationnumber;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsPermitConsolidationNumberType {

    private String etsAccountId;
    private Integer consolidationNumber;
}
