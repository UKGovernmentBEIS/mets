package uk.gov.pmrv.api.migration.emp.corsia.operatordetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpFlightAndAircraftProceduresCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpOperatorDetailsFlightIdentificationCorsia implements EmpCorsiaSection {

	private EmpCorsiaOperatorDetails operatorDetails;

    private EmpFlightAndAircraftProceduresCorsia flightAndAircraftProcedures;

    private ServiceContactDetails serviceContactDetails;
    
    private int empVersion;

    private Map<String, List<EtsFileAttachment>> attachments = new HashMap<>();
}
