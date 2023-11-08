package uk.gov.pmrv.api.aviationreporting.corsia.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpCorsiaOriginatedData {

    private AviationCorsiaOperatorDetails operatorDetails;
}
