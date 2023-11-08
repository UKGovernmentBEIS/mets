package uk.gov.pmrv.api.migration.emp.ukets.monitoringapproach;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.SimplifiedMonitoringApproach;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpMonitoringApproachSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpMigrationMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

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
                       nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts
                  from r1
                 where fldMajorVersion = maxMajorVersion
            ), attEligibility as (
                select p.fldEmitterID,
                       T2.c.value('@name[1]      ', 'nvarchar(max)') Eligibility_documents_uploadedFileName,
                       T2.c.value('@fileName[1]  ', 'nvarchar(max)') Eligibility_documents_storedFileName
                  from r2 p
                 cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname="Eligibility_documents_attach1"])') as T1(c)
                 cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)
                 where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')
            )
            select e.fldEmitterID, e.fldEmitterDisplayID, fldMajorVersion empVersion,
                   nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Simplified_procedures_yes      )[1]', 'nvarchar(max)')), '') Simplified_procedures_yes,
                   nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Tools_used                     )[1]', 'nvarchar(max)')), '') Tools_used,
                   nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Eligibility_information_simplified_calculation_proce)[1]', 'nvarchar(max)')), '') Eligibility_information_simplified_calculation_proce,
                   a1.Eligibility_documents_storedFileName, a1.Eligibility_documents_uploadedFileName
              from tblEmitter e
              join tblAviationEmitter  ae on ae.fldEmitterID = e.fldEmitterID
                                         and ae.fldCommissionListName = 'UK ETS'
              join tlkpEmitterStatus   es on e.fldEmitterStatusID = es.fldEmitterStatusID
                                         and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')
              join      r2                on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'
              left join attEligibility a1 on a1.fldEmitterID = e.fldEmitterID
            """;

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {

        Map<String, EmpMigrationMonitoringApproach> sections =
                queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setEmissionsMonitoringApproach(section.getMonitoringApproach());
                    EmissionsMonitoringPlanMigrationContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                    EmissionsMonitoringPlanUkEtsContainer empContainer = empMigrationContainer.getEmpContainer();
                    EtsFileAttachment attachment = section.getEtsFileAttachment();

                    if (attachment != null) {
                        if (section.getMonitoringApproach().getMonitoringApproachType().equals(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                || section.getMonitoringApproach().getMonitoringApproachType().equals(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY))
                            ((SimplifiedMonitoringApproach) section.getMonitoringApproach())
                                    .getSupportingEvidenceFiles().add(attachment.getUuid());
                        empContainer.getEmpAttachments().put(attachment.getUuid(), attachment.getUploadedFileName());
                        empMigrationContainer.getFileAttachments()
                                .add(etsFileAttachmentMapper.toFileAttachment(attachment));
                    }
                });
    }


    @Override
    public Map<String, EmpMigrationMonitoringApproach> queryEtsSection(List<String> accountIds) {

        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpMonitoringApproach> etsMonitoringApproaches = executeQuery(query, accountIds);

        return etsMonitoringApproaches.stream()
                .collect(Collectors.toMap(EtsEmpMonitoringApproach::getEtsAccountId,
                        EmpMonitoringApproachMigrationMapper::toEmissionsMonitoringApproach));

    }

    private List<EtsEmpMonitoringApproach> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpMonitoringApproachRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }
}
