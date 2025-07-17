package uk.gov.pmrv.api.migration.emp.ukets.additionaldocuments;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.additionaldocuments.EmpAdditionalDocuments;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.additionaldocuments.EmpAdditionalDocumentsRowMapper;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpAdditionalDocumentsSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpAdditionalDocuments>{

	private final JdbcTemplate migrationJdbcTemplate;
	private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

	public EmpAdditionalDocumentsSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate) {
		this.migrationJdbcTemplate = migrationJdbcTemplate;
	}

	private static final String QUERY_BASE = "with XMLNAMESPACES (\r\n"
    		+ "	'urn:www-toplev-com:officeformsofd' AS fd\r\n"
    		+ "), r1 as (\r\n"
    		+ "    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\r\n"
    		+ "           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion\r\n"
    		+ "      from tblEmitter e\r\n"
    		+ "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\r\n"
    		+ "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\r\n"
    		+ "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n"
    		+ "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\r\n"
    		+ "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\r\n"
    		+ "), r2 as (\r\n"
    		+ "    select r1.*,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\r\n"
    		+ "      from r1\r\n"
    		+ "     where fldMajorVersion = maxMajorVersion \r\n"
    		+ "), att as (\r\n"
    		+ "    select fldEmitterID, fldEmitterDisplayID,\r\n"
    		+ "           A.c.value('(@attachname)[1]', 'nvarchar(max)') attachmentFileName\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield)') as A(c)\r\n"
    		+ "), lnk as (\r\n"
    		+ "    select r2.fldEmitterID, r2.fldEmitterDisplayID,\r\n"
    		+ "           L.c.value('(@name)[1]', 'nvarchar(max)') uploadedFileName,\r\n"
    		+ "           L.c.value('(@fileName)[1]', 'nvarchar(max)') storedFileName\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as L(c)\r\n"
    		+ "      left join att on att.fldEmitterID  = r2.fldEmitterID and att.attachmentFileName = L.c.value('(@name)[1]', 'nvarchar(max)')\r\n"
    		+ "     where att.fldEmitterID is null\r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID, e.fldEmitterDisplayID, lnk.uploadedFileName, lnk.storedFileName\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join r2                    on r2.fldEmitterID = e.fldEmitterID and scopeUkEts = 'true'\r\n"
    		+ "  join lnk                   on lnk.fldEmitterID  = e.fldEmitterID ";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {

    	List<String> accountIds = new ArrayList<>(accountsToMigrate.keySet());
    	String query = constructSectionQuery(QUERY_BASE, accountIds);
    	
    	Map<String, List<EtsFileAttachment>> sections = executeQuery(query, accountIds);
    	
    	accountsToMigrate.forEach((etsAccId, account) -> {
    		List<EtsFileAttachment> sectionAttachments = sections.getOrDefault(etsAccId, List.of());
    		EmissionsMonitoringPlanMigrationContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
            EmissionsMonitoringPlanUkEtsContainer empContainer = empMigrationContainer.getEmpContainer();
            
            empContainer.getEmissionsMonitoringPlan().setAdditionalDocuments(toEmpAdditionalDocuments(sectionAttachments));

            sectionAttachments.forEach(file ->
            empContainer.getEmpAttachments().put(file.getUuid(), file.getUploadedFileName()));
            empMigrationContainer.getFileAttachments()
                    .addAll(etsFileAttachmentMapper.toFileAttachments(sectionAttachments));
        });
    }

    @Override
    public Map<String, EmpAdditionalDocuments> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }

	private Map<String, List<EtsFileAttachment>>  executeQuery(String query, List<String> accountIds) {
		List<EtsFileAttachment> etsAdditionalDocuments = migrationJdbcTemplate.query(query,
                new EmpAdditionalDocumentsRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
        return etsAdditionalDocuments
        		.stream()
                .filter(etsFileAttachment -> MigrationConstants.ALLOWED_FILE_TYPES.contains(etsFileAttachment.getUploadedFileName().substring(etsFileAttachment.getUploadedFileName().lastIndexOf(".")).toLowerCase()))
                .collect(Collectors.groupingBy(EtsFileAttachment::getEtsAccountId));
    }
	
	private EmpAdditionalDocuments toEmpAdditionalDocuments(List<EtsFileAttachment> sectionAttachments) {
        return EmpAdditionalDocuments.builder()
                .exist(!ObjectUtils.isEmpty(sectionAttachments))
                .documents(sectionAttachments.stream()
                        .map(EtsFileAttachment::getUuid).collect(Collectors.toSet()))
                .build();
    }
}
