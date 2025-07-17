package uk.gov.pmrv.api.aviationreporting.corsia.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.common.domain.EmpOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpCorsiaOriginatedData extends EmpOriginatedData {

    private AviationCorsiaOperatorDetails operatorDetails;
    
}
