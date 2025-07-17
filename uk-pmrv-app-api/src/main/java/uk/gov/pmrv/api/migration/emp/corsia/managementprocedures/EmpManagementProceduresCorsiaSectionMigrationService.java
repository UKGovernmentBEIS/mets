package uk.gov.pmrv.api.migration.emp.corsia.managementprocedures;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpManagementProceduresCorsiaSectionMigrationService 
	implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpMigrationManagementProceduresCorsia> {

	private final JdbcTemplate migrationJdbcTemplate;

	public EmpManagementProceduresCorsiaSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
		this.migrationJdbcTemplate = migrationJdbcTemplate;
	}

	private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    private static final String QUERY_BASE = """
			with XMLNAMESPACES (
			    'urn:www-toplev-com:officeformsofd' AS fd
			), r1 as (
			    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,
			           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion
			      from tblEmitter e
			      join tblForm F             on f.fldEmitterID = e.fldEmitterID
			      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0
			      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
			      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'
			      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'
			), r2 as (
			    select r1.*,
			           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_corsia_yes)[1]', 'nvarchar(max)'), '') scopeCorsia
			      from r1
			     where fldMajorVersion = maxMajorVersion
			), jobDtl as (
			    select fldEmitterID, string_agg(nullif(trim(T.c.query('Job_title       ').value('.', 'nvarchar(max)')), '') + '|col|' +
			                                    nullif(trim(T.c.query('Responsibilities').value('.', 'nvarchar(max)')), ''), '|row|') Job_details
			      from r2
			     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Corsia_management_details-Job_details/row)') as T(c)
			     group by fldEmitterID
			), attDataFlow as (
			    select p.fldEmitterID,
			           T2.c.value('@name[1]      ', 'nvarchar(max)') Data_flow_attachment_uploadedFileName,
			           T2.c.value('@fileName[1]  ', 'nvarchar(max)') Data_flow_attachment_storedFileName
			      from r2 p
			     cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname="Corsia_management_details:Tree_diagram"])') as T1(c)
			     cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)
			     where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')
			    union all
			    select *
			      from (values
			            ('65E39678-4C8F-4548-8097-9E4F0007876B', 'Emissions compliance data flow 120918.pdf',                         '40924.att'),
			            ('5287B1A7-B8E3-445F-B557-9E4F0007C401', 'ETS - 0011 - Process Map - ETS Emissions Process Flows - V4.1.pdf', '83487.att'),
			            ('562D2C32-32E8-4FFA-979D-9E4F000C7B58', 'DMCA Annex 3 Process Flow Chart.doc',                               '83154.att'),
			            ('DE66C5BA-EFA2-43C5-86BB-A95B00EA3DA2', 'ETS DATA FLOW BLOCK ON BLOCK OFF.pdf',                              '91893.att'),
			            ('ED4BBF19-2C61-494F-8DE3-A99300CB1F4C', '28. xflow-Collect Store Fuel data off block - on block V1.pdf',     '85179.att'),
			            ('DDDCD570-AFB8-41F3-AD5F-A98A00A4B137', 'ETS flow chart Wizz Air Group.pdf',                                 '44002.att'),
			            ('8696A90E-A661-4197-BF3F-AEAC00B06383', 'Figure 1 Flybe Fuel Dataflow.png',                                  '85138.att'),
			            ('69615218-571C-4406-BDBE-AF1C00B23A54', 'Diagrams.pdf',                                                      '91933.att'),
			            ('FD76A35D-C753-4D49-B8BC-A98B00DE85EC', 'Data flow chart - Air Charter Scotland.pdf',                        '64821.att')
			         ) t1 (c1, c2, c3)
			)
			select e.fldEmitterID,
			       m.fldEmitterDisplayID,
			       Job_details,
			       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_management_details-Procedure_description )[1]', 'nvarchar(max)')), '') Corsia_management_details_Procedure_description,
			       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_management_details-Recod_keeping_r_desc  )[1]', 'nvarchar(max)')), '') Corsia_management_details_Recod_keeping_r_des,
			       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_management_details-Risk_desc             )[1]', 'nvarchar(max)')), '') Corsia_management_details_Risk_desc,
			       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_management_details-Revisions_desc        )[1]', 'nvarchar(max)')), '') Corsia_management_details_Revisions_desc,
			       a1.Data_flow_attachment_storedFileName, a1.Data_flow_attachment_uploadedFileName
			   
			  from tblEmitter e
			  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID
			                            and ae.fldCommissionListName = 'UK ETS'
			  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID
			                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')
			  join  r2                   on r2.fldEmitterID = e.fldEmitterID and scopeCorsia = 'true'
			  left join jobDtl        jd on jd.fldEmitterID = e.fldEmitterID
			  left join attDataFlow   a1 on a1.fldEmitterID = e.fldEmitterID
			  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'
			""";
    
	@Override
	public void populateSection(Map<String, Account> accountsToMigrate,
			Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {
		Map<String, EmpMigrationManagementProceduresCorsia> sections =
                querySection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setManagementProcedures(section.getManagementProcedures());
                    EmissionsMonitoringPlanMigrationCorsiaContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                    EmissionsMonitoringPlanCorsiaContainer empContainer = empMigrationContainer.getEmpContainer();
                    EtsFileAttachment attachment = section.getDataFlowFileAttachment();

                    if (attachment != null) {
                        empContainer.getEmpAttachments().put(attachment.getUuid(), attachment.getUploadedFileName());
                        empMigrationContainer.getFileAttachments()
                                .add(etsFileAttachmentMapper.toFileAttachment(attachment));
                    }
                });
		
	}

    @Override
    public Map<String, EmpMigrationManagementProceduresCorsia> querySection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpManagementProceduresCorsia> managementProcedures = executeQuery(query, accountIds);

        return managementProcedures.stream()
                .collect(Collectors.toMap(EtsEmpManagementProceduresCorsia::getFldEmitterID,
                		EmpManagementProceduresCorsiaMigrationMapper::toEmpManagementProcedures));

    }

    private List<EtsEmpManagementProceduresCorsia> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpManagementProceduresCorsiaRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
