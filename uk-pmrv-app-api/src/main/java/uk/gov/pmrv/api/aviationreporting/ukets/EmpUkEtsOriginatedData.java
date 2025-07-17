package uk.gov.pmrv.api.aviationreporting.ukets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.common.domain.EmpOriginatedData;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpUkEtsOriginatedData extends EmpOriginatedData {

    private AviationOperatorDetails operatorDetails;
}
