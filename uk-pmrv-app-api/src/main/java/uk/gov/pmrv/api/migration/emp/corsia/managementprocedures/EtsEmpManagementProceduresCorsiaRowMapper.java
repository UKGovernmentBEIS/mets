package uk.gov.pmrv.api.migration.emp.corsia.managementprocedures;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class EtsEmpManagementProceduresCorsiaRowMapper implements RowMapper<EtsEmpManagementProceduresCorsia> {
	
	@Override
    public EtsEmpManagementProceduresCorsia mapRow(ResultSet rs, int rowNum) throws SQLException {
        
		return EtsEmpManagementProceduresCorsia.builder()
            .fldEmitterID(rs.getString("fldEmitterID"))
            .fldEmitterDisplayID(rs.getString("fldEmitterDisplayID"))
            .jobDetails(Stream.of(rs.getString("Job_details").split("\\|row\\|"))
                    .filter(s -> !s.isEmpty())
                    .map(String::trim)
                    .toList())
            .procedureDescription(rs.getString("Corsia_management_details_Procedure_description"))
            .recordKeepingDescription(rs.getString("Corsia_management_details_Recod_keeping_r_des"))
            .riskDescription(rs.getString("Corsia_management_details_Risk_desc"))
            .revisionsDescription(rs.getString("Corsia_management_details_Revisions_desc"))
            .storedFileName(rs.getString("Data_flow_attachment_storedFileName"))
            .uploadedFileName(rs.getString("Data_flow_attachment_uploadedFileName"))
            .build();
    }
}
