package uk.gov.pmrv.api.migration.emp.ukets.managementprocedures;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsEmpManagementProceduresRowMapper implements RowMapper<EtsEmpManagementProcedures> {


    @Override
    public EtsEmpManagementProcedures mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsEmpManagementProcedures.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .jobDetails(Stream.of(rs.getString("Job_details").split("\\|row\\|"))
                        .filter(s -> !s.isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toList()))
                .controlActivitiesRecordingProceduresProcedureTitle(rs.getString("Control_activities_recording_procedures_Procedure_title"))
                .controlActivitiesRecordingProceduresProcedureReference(rs.getString("Control_activities_recording_procedures_Procedure_reference"))
                .controlActivitiesRecordingProceduresProcedureDescription(rs.getString("Control_activities_recording_procedures_Procedure_description"))
                .controlActivitiesRecordingProceduresDataMaintenancePost(rs.getString("Control_activities_recording_procedures_Data_maintenance_post"))
                .controlActivitiesRecordingProceduresRecordsLocation(rs.getString("Control_activities_recording_procedures_Records_location"))
                .controlActivitiesRecordingProceduresSystemName(rs.getString("Control_activities_recording_procedures_System_name"))

                .managementDetailsProcedureTitle(rs.getString("Management_details_Procedure_title"))
                .managementDetailsProcedureReference(rs.getString("Management_details_Procedure_reference"))
                .managementDetailsProcedureDescription(rs.getString("Management_details_Procedure_description"))
                .managementDetailsDataMaintenancePost(rs.getString("Management_details_Data_maintenance_post"))
                .managementDetailsRecordsLocation(rs.getString("Management_details_Records_location"))
                .managementDetailsSystemName(rs.getString("Management_details_System_name"))

                .managementDetailsProcedureRegularTitle(rs.getString("Management_details_Procedure_regular_title"))
                .managementDetailsProcedureRegularReference(rs.getString("Management_details_Procedure_regular_reference"))
                .managementDetailsProcedureRegularDescription(rs.getString("Management_details_Procedure_regular_desc"))
                .managementDetailsRegularDataMaintenancePost(rs.getString("Management_details_Procedure_regular_data"))
                .managementDetailsRegularRecordsLocation(rs.getString("Management_details_Procedure_regular_location"))
                .managementDetailsRegularSystemName(rs.getString("Management_details_Procedure_regular_system_used"))

                .controlActivitiesQualityProceduresProcedureTitle(rs.getString("Control_activities_quality_procedures_Procedure_title"))
                .controlActivitiesQualityProceduresProcedureReference(rs.getString("Control_activities_quality_procedures_Procedure_reference"))
                .controlActivitiesQualityProceduresProcedureDescription(rs.getString("Control_activities_quality_procedures_Procedure_description"))
                .controlActivitiesQualityProceduresDataMaintenancePost(rs.getString("Control_activities_quality_procedures_Data_maintenance_post"))
                .controlActivitiesQualityProceduresRecordsLocation(rs.getString("Control_activities_quality_procedures_Records_location"))
                .controlActivitiesQualityProceduresSystemName(rs.getString("Control_activities_quality_procedures_System_name"))

                .controlActivitiesIntervalProceduresProcedureTitle(rs.getString("Control_activities_interval_procedures_Procedure_title"))
                .controlActivitiesIntervalProceduresProcedureReference(rs.getString("Control_activities_interval_procedures_Procedure_reference"))
                .controlActivitiesIntervalProceduresProcedureDescription(rs.getString("Control_activities_interval_procedures_Procedure_description"))
                .controlActivitiesIntervalProceduresDataMaintenancePost(rs.getString("Control_activities_interval_procedures_Data_maintenance_post"))
                .controlActivitiesIntervalProceduresRecordsLocation(rs.getString("Control_activities_interval_procedures_Records_location"))
                .controlActivitiesIntervalProceduresSystemName(rs.getString("Control_activities_interval_procedures_System_name"))

                .controlActivitiesCorrectiveProceduresProcedureTitle(rs.getString("Control_activities_corrective_procedures_Procedure_title"))
                .controlActivitiesCorrectiveProceduresProcedureReference(rs.getString("Control_activities_corrective_procedures_Procedure_reference"))
                .controlActivitiesCorrectiveProceduresProcedureDescription(rs.getString("Control_activities_corrective_procedures_Procedure_description"))
                .controlActivitiesCorrectiveProceduresDataMaintenancePost(rs.getString("Control_activities_corrective_procedures_Data_maintenance_post"))
                .controlActivitiesCorrectiveProceduresRecordsLocation(rs.getString("Control_activities_corrective_procedures_Records_location"))
                .controlActivitiesCorrectiveProceduresSystemName(rs.getString("Control_activities_corrective_procedures_System_name"))

                .controlOutsourcedActivitiesProceduresProcedureTitle(rs.getString("Control_outsourced_activities_procedures_Procedure_title"))
                .controlOutsourcedActivitiesProceduresProcedureReference(rs.getString("Control_outsourced_activities_procedures_Procedure_reference"))
                .controlOutsourcedActivitiesProceduresProcedureDescription(rs.getString("Control_outsourced_activities_procedures_Procedure_description"))
                .controlOutsourcedActivitiesProceduresDataMaintenancePost(rs.getString("Control_outsourced_activities_procedures_Data_maintenance_post"))
                .controlOutsourcedActivitiesProceduresRecordsLocation(rs.getString("Control_outsourced_activities_procedures_Records_location"))
                .controlOutsourcedActivitiesProceduresSystemName(rs.getString("Control_outsourced_activities_procedures_System_name"))

                .controlActivitiesRiskProceduresProcedureTitle(rs.getString("Control_activities_risk_procedures_Procedure_title"))
                .controlActivitiesRiskProceduresProcedureReference(rs.getString("Control_activities_risk_procedures_Procedure_reference"))
                .controlActivitiesRiskProceduresProcedureDescription(rs.getString("Control_activities_risk_procedures_Procedure_description"))
                .controlActivitiesRiskProceduresDataMaintenancePost(rs.getString("Control_activities_risk_procedures_Data_maintenance_post"))
                .controlActivitiesRiskProceduresRecordsLocation(rs.getString("Control_activities_risk_procedures_Records_location"))
                .controlActivitiesRiskProceduresSystemName(rs.getString("Control_activities_risk_procedures_System_name"))

                .dataFlowActivitiesProceduresProcedureTitle(rs.getString("Data_flow_activities_procedures_Procedure_title"))
                .dataFlowActivitiesProceduresProcedureReference(rs.getString("Data_flow_activities_procedures_Procedure_reference"))
                .dataFlowActivitiesProceduresProcedureDescription(rs.getString("Data_flow_activities_procedures_Procedure_description"))
                .dataFlowActivitiesProceduresDataMaintenancePost(rs.getString("Data_flow_activities_procedures_Procedure_responsible_post_department"))
                .dataFlowActivitiesProceduresRecordsLocation(rs.getString("Data_flow_activities_procedures_Procedure_location"))
                .dataFlowActivitiesProceduresSystemName(rs.getString("Data_flow_activities_procedures_Procedure_it_system"))
                .dataFlowActivitiesProceduresDiagramReference(rs.getString("Data_flow_activities_procedures_Procedure_diagram_reference"))
                .dataFlowActivitiesProceduresOtherStandardsApplied(rs.getString("Data_flow_activities_procedures_Procedure_cen_or_other_standards_applied"))
                .dataFlowActivitiesProceduresPrimaryDataSources(rs.getString("Data_flow_activities_procedures_primary_data"))
                .dataFlowActivitiesProceduresProcessingSteps(rs.getString("Data_flow_activities_procedures_description"))
                .dataFlowAttachmentStoredFileName(rs.getString("Data_flow_attachment_storedFileName"))
                .dataFlowAttachmentUploadedFileName(rs.getString("Data_flow_attachment_uploadedFileName"))

                .isEmsCertified(rs.getBoolean("Ems_certified_by_ao_yes"))
                .emsDocumentation(rs.getString("Ems_documentation"))
                .standard(rs.getString("Standard"))

                .riskAssessmentStoredFileName(rs.getString("Result_of_control_activities_storedFileName"))
                .riskAssessmentUploadedFileName(rs.getString("Result_of_control_activities_uploadedFileName"))

                .crossChecksProcedureDetailsProcedureTitle(rs.getString("Cross_check_procedure_details_Procedure_title"))
                .crossChecksProcedureDetailsProcedureReference(rs.getString("Cross_check_procedure_details_Procedure_reference"))
                .crossChecksProcedureDetailsProcedureDescription(rs.getString("Cross_check_procedure_details_Procedure_description"))
                .crossChecksProcedureDetailsDataMaintenancePost(rs.getString("Cross_check_procedure_details_Data_maintenance_post"))
                .crossChecksProcedureDetailsRecordsLocation(rs.getString("Cross_check_procedure_details_Records_location"))
                .crossChecksProcedureDetailsSystemName(rs.getString("Cross_check_procedure_details_System_name"))
                .build();

    }
}
