package uk.gov.pmrv.api.aviationreporting.ukets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpUkEtsOriginatedData {

    private AviationOperatorDetails operatorDetails;
}
