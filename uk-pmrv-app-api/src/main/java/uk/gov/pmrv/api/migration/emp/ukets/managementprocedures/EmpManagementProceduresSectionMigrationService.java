package uk.gov.pmrv.api.migration.emp.ukets.managementprocedures;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.attachments.FileAttachmentUtil;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpManagementProceduresSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpMigrationManagementProcedures> {

    private final JdbcTemplate migrationJdbcTemplate;

    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

	public EmpManagementProceduresSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
		this.migrationJdbcTemplate = migrationJdbcTemplate;
	}

	private static final String BLANK_FILE_PATH = "migration" + File.separator + "attachments" + File.separator + "Blank file Risk assessment not required.pdf";
    private static final String QUERY_BASE = "with XMLNAMESPACES (\n"
    		+ "    'urn:www-toplev-com:officeformsofd' AS fd\n"
    		+ "), r1 as (\n"
    		+ "    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\n"
    		+ "           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion\n"
    		+ "      from tblEmitter e\n"
    		+ "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\n"
    		+ "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\n"
    		+ "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\n"
    		+ "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\n"
    		+ "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\n"
    		+ "), r2 as (\n"
    		+ "    select r1.*,\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\n"
    		+ "      from r1\n"
    		+ "     where fldMajorVersion = maxMajorVersion \n"
    		+ "), jobDtl as (\n"
    		+ "    select fldEmitterID, string_agg(nullif(trim(T.c.query('Job_title       ').value('.', 'nvarchar(max)')), '') + '|col|' +\n"
    		+ "                                    nullif(trim(T.c.query('Responsibilities').value('.', 'nvarchar(max)')), ''), '|row|') Job_details\n"
    		+ "      from r2\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Management_details-Job_details/row)') as T(c)\n"
    		+ "     group by fldEmitterID\n"
    		+ "), attDataFlow as (\n"
    		+ "    select p.fldEmitterID,\n"
    		+ "           T2.c.value('@name[1]      ', 'nvarchar(max)') Data_flow_attachment_uploadedFileName,\n"
    		+ "           T2.c.value('@fileName[1]  ', 'nvarchar(max)') Data_flow_attachment_storedFileName\n"
    		+ "      from r2 p\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname=\"Data_flow_attachment\"])') as T1(c)\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)\n"
    		+ "     where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')\n"
    		+ "), attResCtrlAct as (\n"
    		+ "    select p.fldEmitterID,\n"
    		+ "           T2.c.value('@name[1]      ', 'nvarchar(max)') Result_of_control_activities_uploadedFileName,\n"
    		+ "           T2.c.value('@fileName[1]  ', 'nvarchar(max)') Result_of_control_activities_storedFileName\n"
    		+ "      from r2 p\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname=\"Result_of_control_activities\"])') as T1(c)\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)\n"
    		+ "     where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')\n"
    		+ "    union all\n"
    		+ "    select *\n"
    		+ "      from (values\n"
    		+ "            ('804EB9B7-A72C-4612-A33B-ACBD0159E43A', 'GESTAIR RISK Assesment ETS  CORSIA.pdf', '69460.att'),\n"
    		+ "            ('EC1E0910-20E0-4DBD-8767-ACBD0159F776', 'Risk assessement UK ETS.xlsx', '75625.att'),\n"
    		+ "            ('AD9190DC-D4F1-4532-8F6B-ACD9010742F6', 'Risk Assessment.pdf', '72852.att'),\n"
    		+ "            ('EBA657BC-D9A7-4877-95C2-ADD000C6F97D', 'Risk Assessment- FlyPlay.xlsx', '79094.att')\n"
    		+ "         ) t1 (c1, c2, c3)\n"
    		+ ")\n"
    		+ "select e.fldEmitterID, e.fldEmitterDisplayID,  \n"
    		+ "       Job_details,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_title                                      )[1]', 'nvarchar(max)')), '') Management_details_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_reference                                  )[1]', 'nvarchar(max)')), '') Management_details_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_description                                )[1]', 'nvarchar(max)')), '') Management_details_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Data_maintenance_post                                )[1]', 'nvarchar(max)')), '') Management_details_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Records_location                                     )[1]', 'nvarchar(max)')), '') Management_details_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-System_name                                          )[1]', 'nvarchar(max)')), '') Management_details_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_regular_title                              )[1]', 'nvarchar(max)')), '') Management_details_Procedure_regular_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_regular_reference                          )[1]', 'nvarchar(max)')), '') Management_details_Procedure_regular_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_regular_desc                               )[1]', 'nvarchar(max)')), '') Management_details_Procedure_regular_desc       ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_regular_data                               )[1]', 'nvarchar(max)')), '') Management_details_Procedure_regular_data       ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_regular_location                           )[1]', 'nvarchar(max)')), '') Management_details_Procedure_regular_location   ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Management_details-Procedure_regular_system_used                        )[1]', 'nvarchar(max)')), '') Management_details_Procedure_regular_system_used,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_risk_procedures-Procedure_title                      )[1]', 'nvarchar(max)')), '') Control_activities_risk_procedures_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_risk_procedures-Procedure_reference                  )[1]', 'nvarchar(max)')), '') Control_activities_risk_procedures_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_risk_procedures-Procedure_description                )[1]', 'nvarchar(max)')), '') Control_activities_risk_procedures_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_risk_procedures-Data_maintenance_post                )[1]', 'nvarchar(max)')), '') Control_activities_risk_procedures_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_risk_procedures-Records_location                     )[1]', 'nvarchar(max)')), '') Control_activities_risk_procedures_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_risk_procedures-System_name                          )[1]', 'nvarchar(max)')), '') Control_activities_risk_procedures_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_quality_procedures-Procedure_title                   )[1]', 'nvarchar(max)')), '') Control_activities_quality_procedures_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_quality_procedures-Procedure_reference               )[1]', 'nvarchar(max)')), '') Control_activities_quality_procedures_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_quality_procedures-Procedure_description             )[1]', 'nvarchar(max)')), '') Control_activities_quality_procedures_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_quality_procedures-Data_maintenance_post             )[1]', 'nvarchar(max)')), '') Control_activities_quality_procedures_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_quality_procedures-Records_location                  )[1]', 'nvarchar(max)')), '') Control_activities_quality_procedures_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_quality_procedures-System_name                       )[1]', 'nvarchar(max)')), '') Control_activities_quality_procedures_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_interval_procedures-Procedure_title                  )[1]', 'nvarchar(max)')), '') Control_activities_interval_procedures_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_interval_procedures-Procedure_reference              )[1]', 'nvarchar(max)')), '') Control_activities_interval_procedures_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_interval_procedures-Procedure_description            )[1]', 'nvarchar(max)')), '') Control_activities_interval_procedures_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_interval_procedures-Data_maintenance_post            )[1]', 'nvarchar(max)')), '') Control_activities_interval_procedures_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_interval_procedures-Records_location                 )[1]', 'nvarchar(max)')), '') Control_activities_interval_procedures_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_interval_procedures-System_name                      )[1]', 'nvarchar(max)')), '') Control_activities_interval_procedures_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_corrective_procedures-Procedure_title                )[1]', 'nvarchar(max)')), '') Control_activities_corrective_procedures_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_corrective_procedures-Procedure_reference            )[1]', 'nvarchar(max)')), '') Control_activities_corrective_procedures_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_corrective_procedures-Procedure_description          )[1]', 'nvarchar(max)')), '') Control_activities_corrective_procedures_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_corrective_procedures-Data_maintenance_post          )[1]', 'nvarchar(max)')), '') Control_activities_corrective_procedures_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_corrective_procedures-Records_location               )[1]', 'nvarchar(max)')), '') Control_activities_corrective_procedures_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_corrective_procedures-System_name                    )[1]', 'nvarchar(max)')), '') Control_activities_corrective_procedures_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_outsourced_activities_procedures-Procedure_title                )[1]', 'nvarchar(max)')), '') Control_outsourced_activities_procedures_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_outsourced_activities_procedures-Procedure_reference            )[1]', 'nvarchar(max)')), '') Control_outsourced_activities_procedures_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_outsourced_activities_procedures-Procedure_description          )[1]', 'nvarchar(max)')), '') Control_outsourced_activities_procedures_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_outsourced_activities_procedures-Data_maintenance_post          )[1]', 'nvarchar(max)')), '') Control_outsourced_activities_procedures_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_outsourced_activities_procedures-Records_location               )[1]', 'nvarchar(max)')), '') Control_outsourced_activities_procedures_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_outsourced_activities_procedures-System_name                    )[1]', 'nvarchar(max)')), '') Control_outsourced_activities_procedures_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_recording_procedures-Procedure_title                 )[1]', 'nvarchar(max)')), '') Control_activities_recording_procedures_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_recording_procedures-Procedure_reference             )[1]', 'nvarchar(max)')), '') Control_activities_recording_procedures_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_recording_procedures-Procedure_description           )[1]', 'nvarchar(max)')), '') Control_activities_recording_procedures_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_recording_procedures-Data_maintenance_post           )[1]', 'nvarchar(max)')), '') Control_activities_recording_procedures_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_recording_procedures-Records_location                )[1]', 'nvarchar(max)')), '') Control_activities_recording_procedures_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Control_activities_recording_procedures-System_name                     )[1]', 'nvarchar(max)')), '') Control_activities_recording_procedures_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_title                         )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_title                         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_reference                     )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_reference                     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_diagram_reference             )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_diagram_reference             ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_description                   )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_description                   ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_responsible_post_department   )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_responsible_post_department   ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_location                      )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_location                      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_it_system                     )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_it_system                     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures-Procedure_cen_or_other_standards_applied)[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_Procedure_cen_or_other_standards_applied,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures_primary_data                            )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_primary_data                            ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_flow_activities_procedures_description                             )[1]', 'nvarchar(max)')), '') Data_flow_activities_procedures_description                             ,\n"
    		+ "       a1.Data_flow_attachment_storedFileName, a1.Data_flow_attachment_uploadedFileName,\n"
    		+ "\n"
    		+ "       a2.Result_of_control_activities_storedFileName, a2.Result_of_control_activities_uploadedFileName,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cross_check_procedure_details-Procedure_title                           )[1]', 'nvarchar(max)')), '') Cross_check_procedure_details_Procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cross_check_procedure_details-Procedure_reference                       )[1]', 'nvarchar(max)')), '') Cross_check_procedure_details_Procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cross_check_procedure_details-Procedure_description                     )[1]', 'nvarchar(max)')), '') Cross_check_procedure_details_Procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cross_check_procedure_details-Data_maintenance_post                     )[1]', 'nvarchar(max)')), '') Cross_check_procedure_details_Data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cross_check_procedure_details-Records_location                          )[1]', 'nvarchar(max)')), '') Cross_check_procedure_details_Records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cross_check_procedure_details-System_name                               )[1]', 'nvarchar(max)')), '') Cross_check_procedure_details_System_name          ,\n"
    		+ "\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Ems_documentation                                                       )[1]', 'nvarchar(max)')), '') Ems_documentation      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Ems_certified_by_ao_yes                                                 )[1]', 'nvarchar(max)')), '') Ems_certified_by_ao_yes,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Standard                                                                )[1]', 'nvarchar(max)')), '') Standard\n"
    		+ "\n"
    		+ "  from tblEmitter e\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\n"
    		+ "  join  r2                   on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\n"
    		+ "  left join jobDtl        jd on jd.fldEmitterID = e.fldEmitterID\n"
    		+ "  left join attDataFlow   a1 on a1.fldEmitterID = e.fldEmitterID\n"
    		+ "  left join attResCtrlAct a2 on a2.fldEmitterID = e.fldEmitterID\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {

        Map<String, EmpMigrationManagementProcedures> sections =
                queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                    EmissionsMonitoringPlanMigrationContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                    EmissionsMonitoringPlanUkEtsContainer empContainer = empMigrationContainer.getEmpContainer();
                    EmpManagementProcedures sectionManagementProcedures = section.getManagementProcedures();
                    EmissionsMonitoringApproachType monitoringApproachType = empContainer
                    		.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach().getMonitoringApproachType();
                    
                    // add only necessary data according to the monitoring approach and emission sources
                    EmpManagementProcedures managementProcedures = EmpManagementProcedures
                    		.builder()
                    		.monitoringReportingRoles(sectionManagementProcedures.getMonitoringReportingRoles())
                    		.recordKeepingAndDocumentation(sectionManagementProcedures.getRecordKeepingAndDocumentation())
                    		.build();
                    if (!EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY.equals(monitoringApproachType)) {
                    	managementProcedures.setMonitoringPlanAppropriateness(sectionManagementProcedures.getMonitoringPlanAppropriateness());
                    	managementProcedures.setAssignmentOfResponsibilities(sectionManagementProcedures.getAssignmentOfResponsibilities());
                    	managementProcedures.setControlOfOutsourcedActivities(sectionManagementProcedures.getControlOfOutsourcedActivities());
                    	managementProcedures.setCorrectionsAndCorrectiveActions(sectionManagementProcedures.getCorrectionsAndCorrectiveActions());
                    	managementProcedures.setDataValidation(sectionManagementProcedures.getDataValidation());
                    	managementProcedures.setDataFlowActivities(sectionManagementProcedures.getDataFlowActivities());
                    	managementProcedures.setAssessAndControlRisks(sectionManagementProcedures.getAssessAndControlRisks());
                    	managementProcedures.setQaMeteringAndMeasuringEquipment(sectionManagementProcedures.getQaMeteringAndMeasuringEquipment());
                    	managementProcedures.setEnvironmentalManagementSystem(sectionManagementProcedures.getEnvironmentalManagementSystem());
                    }
                    if (EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType)) {
                    	managementProcedures.setRiskAssessmentFile(sectionManagementProcedures.getRiskAssessmentFile());
                    	if (!onlyBlockOnBlockOffMethodExists(empContainer)) {
                    		managementProcedures.setUpliftQuantityCrossChecks(sectionManagementProcedures.getUpliftQuantityCrossChecks());
                    	}
                    }
                    empContainer.getEmissionsMonitoringPlan().setManagementProcedures(managementProcedures);
                    
                    // handle attachments
                    EtsFileAttachment dataFlowAttachment = section.getDataFlowFileAttachment();
                    EtsFileAttachment riskAssessmentAttachment = section.getRiskAssessmentFileAttachment();

                    if (dataFlowAttachment != null && 
                    		!EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY.equals(monitoringApproachType)) {
                        empContainer.getEmpAttachments().put(dataFlowAttachment.getUuid(), dataFlowAttachment.getUploadedFileName());
                        empMigrationContainer.getFileAttachments()
                                .add(etsFileAttachmentMapper.toFileAttachment(dataFlowAttachment));
                    }

                    if (EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType)) {
                    	if (riskAssessmentAttachment != null) {
                            empContainer.getEmpAttachments().put(riskAssessmentAttachment.getUuid(), riskAssessmentAttachment.getUploadedFileName());
                            empMigrationContainer.getFileAttachments()
                                    .add(etsFileAttachmentMapper.toFileAttachment(riskAssessmentAttachment));
                        } else {
                        	FileAttachment blankFileAttachment = FileAttachmentUtil.getFileAttachment(BLANK_FILE_PATH);
                        	UUID blankFileAttachmentUuid = UUID.fromString(blankFileAttachment.getUuid());
                        	empContainer.getEmissionsMonitoringPlan().getManagementProcedures().setRiskAssessmentFile(blankFileAttachmentUuid);
                        	empContainer.getEmpAttachments().put(blankFileAttachmentUuid, blankFileAttachment.getFileName());
                            empMigrationContainer.getFileAttachments().add(blankFileAttachment);
                        }
                    }                 
                });
    }

    @Override
    public Map<String, EmpMigrationManagementProcedures> queryEtsSection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpManagementProcedures> etsManagementProcedures = executeQuery(query, accountIds);

        return etsManagementProcedures.stream()
                .collect(Collectors.toMap(EtsEmpManagementProcedures::getEtsAccountId,
                        EmpManagementProceduresMigrationMapper::toManagementProcedures));
    }

    private List<EtsEmpManagementProcedures> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpManagementProceduresRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
    
    private boolean onlyBlockOnBlockOffMethodExists(EmissionsMonitoringPlanUkEtsContainer empContainer) {
		return empContainer.getEmissionsMonitoringPlan()
				.getEmissionSources()
				.getAircraftTypes()
				.stream()
		        .map(AircraftTypeDetails::getFuelConsumptionMeasuringMethod)
		        .allMatch(FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF::equals);
	}
}
