package uk.gov.pmrv.api.migration.emp.corsia.monitoringapproach;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.CertMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
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
public class EmpMonitoringApproachCorsiaSectionMigrationService 
	implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpMigrationMonitoringApproachCorsia> {

	private final JdbcTemplate migrationJdbcTemplate;

	public EmpMonitoringApproachCorsiaSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
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
			), attEvidence as (
			    select p.fldEmitterID,
			           T2.c.value('@name[1]      ', 'nvarchar(max)') evidence_uploadedFileName,
			           T2.c.value('@fileName[1]  ', 'nvarchar(max)') evidence_storedFileName
			      from r2 p
			     cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname="CERT_output_attach"])') as T1(c)
			     cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)
			     where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')
			)
			select e.fldEmitterID,
			       m.fldEmitterDisplayID,
			       fldMajorVersion empVersion,
			       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_monitoring_method )[1]', 'nvarchar(max)')), '') Corsia_monitoring_method,
			       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_cert_type         )[1]', 'nvarchar(max)')), '') Corsia_cert_type,
			       coalesce(
			           nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Icao_cert_used_desc      )[1]', 'nvarchar(max)')), ''),
			           nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Icao_cert_not_used       )[1]', 'nvarchar(max)')), '')
			       ) Icao_cert_used_desc,
			       a1.evidence_storedFileName, a1.evidence_uploadedFileName
			  from tblEmitter e
			  join tblAviationEmitter  ae on ae.fldEmitterID = e.fldEmitterID
			                             and ae.fldCommissionListName = 'UK ETS'
			  join tlkpEmitterStatus   es on e.fldEmitterStatusID = es.fldEmitterStatusID
			                             and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')
			  join      r2                on r2.fldEmitterID = e.fldEmitterID and scopeCorsia = 'true'
			  left join attEvidence    a1 on a1.fldEmitterID = e.fldEmitterID
			  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'
			""";
    
	@Override
	public void populateSection(Map<String, Account> accountsToMigrate,
			Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {
		Map<String, EmpMigrationMonitoringApproachCorsia> sections =
                querySection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setEmissionsMonitoringApproach(section.getMonitoringApproach());
                    EmissionsMonitoringPlanMigrationCorsiaContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                    EmissionsMonitoringPlanCorsiaContainer empContainer = empMigrationContainer.getEmpContainer();
                    EtsFileAttachment attachment = section.getEtsFileAttachment();

                    if (attachment != null) {
                        if (section.getMonitoringApproach().getMonitoringApproachType().equals(EmissionsMonitoringApproachTypeCorsia.CERT_MONITORING))
                            ((CertMonitoringApproach) section.getMonitoringApproach())
                                    .getSupportingEvidenceFiles().add(attachment.getUuid());
                        empContainer.getEmpAttachments().put(attachment.getUuid(), attachment.getUploadedFileName());
                        empMigrationContainer.getFileAttachments()
                                .add(etsFileAttachmentMapper.toFileAttachment(attachment));
                    }
                });
		
	}

    @Override
    public Map<String, EmpMigrationMonitoringApproachCorsia> querySection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsMonitoringApproachCorsia> monitoringApproaches = executeQuery(query, accountIds);

        return monitoringApproaches.stream()
                .collect(Collectors.toMap(EtsMonitoringApproachCorsia::getFldEmitterID,
                		EmpMonitoringApproachCorsiaMigrationMapper::toEmissionsMonitoringApproach));

    }

    private List<EtsMonitoringApproachCorsia> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsMonitoringApproachCorsiaRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
