package uk.gov.pmrv.api.migration.permit.monitoringmethodologyplan;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentRowMapper;

@Service
@RequiredArgsConstructor
public class EtsApprovedMMPFileQueryService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final EtsFileAttachmentRowMapper fileAttachmentRowMapper;
    private static final String QUERY_BASE =
            """
                    with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd),
                    allPermits as (
                        select f.fldEmitterID, f.fldFormID,
                               fd.fldMajorVersion, max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion,
                               fd.fldDateUpdated, fd.fldFormDataID
                          from tblForm f
                          join tblFormData fd on fd.fldFormID = f.fldFormID            and fd.fldMinorVersion = 0
                          join tlnkFormTypePhase ftp on ftp.fldFormTypePhaseID = f.fldFormTypePhaseID
                          join tlkpFormType ft on ft.fldFormTypeID = ftp.fldFormTypeID and ft.fldName = 'IN_PermitApplication'
                          join tlkpPhase p on p.fldPhaseID = ftp.fldPhaseID            and p.fldDisplayName = 'Phase 3'
                    ), latestPermit as (
                        select p.fldEmitterID, e.fldEmitterDisplayID, fd.*
                          from allPermits p
                          join tblFormData fd on fd.fldFormDataID = p.fldFormDataID
                          join tblEmitter e on e.fldEmitterID = p.fldEmitterID
                          join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID and (es.fldDisplayName = 'Live' or p.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
                          join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID and et.fldName = 'Installation'
                         where p.fldMajorVersion = p.maxMajorVersion
                    ), mmp as (
                        select p.fldEmitterID, p.fldEmitterDisplayID,
                               T1.c.value('@attachname[1]', 'nvarchar(max)') approved_mmp
                          from latestPermit p
                         cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname="Mmp_approved_mmp"])') as T1(c)
                    ), att as (
                        select fldEmitterID,
                               T.c.value('(@name)[1]', 'nvarchar(max)') uploadedFileName,
                               T.c.value('(@fileName)[1]', 'nvarchar(max)') storedFileName
                          from latestPermit
                         cross apply fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T(c)
                    )
                    select e.*, e.fldEmitterID emitterId from mmp join att e on e.fldEmitterID = mmp.fldEmitterID and e.uploadedFileName = mmp.approved_mmp 
                    """;

    public Map<String, List<EtsFileAttachment>> query(List<String> accountIds) {
        return migrationJdbcTemplate
                .query(constructEtsSectionQuery(QUERY_BASE, accountIds),
                        new ArgumentPreparedStatementSetter(accountIds.isEmpty() ? new Object[] {} : accountIds.toArray()),
                        fileAttachmentRowMapper)
                .stream()
                .filter(etsFileAttachment -> MigrationConstants.ALLOWED_FILE_TYPES.contains(etsFileAttachment.getUploadedFileName().substring(etsFileAttachment.getUploadedFileName().lastIndexOf(".")).toLowerCase()))
                .collect(Collectors.groupingBy(EtsFileAttachment::getEtsAccountId));
    }
}
