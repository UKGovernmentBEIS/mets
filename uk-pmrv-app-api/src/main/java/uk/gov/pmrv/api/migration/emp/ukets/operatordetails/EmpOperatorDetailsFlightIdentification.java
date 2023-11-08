package uk.gov.pmrv.api.migration.emp.ukets.operatordetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.flightaircraftprocedures.EmpFlightAndAircraftProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpOperatorDetailsFlightIdentification implements EmpUkEtsSection {

    private EmpOperatorDetails operatorDetails;

    private EmpFlightAndAircraftProcedures flightAndAircraftProcedures;

    private ServiceContactDetails serviceContactDetails;

    private Map<String, List<EtsFileAttachment>> attachments = new HashMap<>();
}
