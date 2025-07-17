package uk.gov.pmrv.api.migration.installationaccount;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IMPORTANT NOTE :
 * In order to migrate installation accounts, account identification(mandatory), legal entities (mandatory) and verification bodies (optional)
 * should have been migrated previously.
 */
@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
public class MigrationInstallationAccountService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final AccountDTOBuilder accountDTOBuilder;
    private final MigrationInstallationAccountCreationService installationAccountCreationService;

    public MigrationInstallationAccountService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                               Validator validator,
                                               AccountDTOBuilder accountDTOBuilder,
                                               MigrationInstallationAccountCreationService installationAccountCreationService) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.validator = validator;
        this.accountDTOBuilder = accountDTOBuilder;
        this.installationAccountCreationService = installationAccountCreationService;
    }

    private static final String QUERY_BASE  = """
        with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd),
        allPermits AS (
            select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID
              from tblForm F
              join tblFormData FD ON FD.fldFormID = F.fldFormID
              join tlnkFormTypePhase FTP ON F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
              join tlkpFormType FT ON FTP.fldFormTypeID = FT.fldFormTypeID
              join tlkpPhase P ON FTP.fldPhaseID = P.fldPhaseID
             where FD.fldMinorVersion = 0 AND P.fldDisplayName = 'Phase 3' AND FT.fldName = 'IN_PermitApplication'
        ),
        mxPVer AS (
            select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion'
              from allPermits
             group by fldFormID
        ),
        latestPermit AS (
            select p.fldEmitterID, FD.*
              from allPermits p
              join mxPVer ON p.fldFormID = mxPVer.FormID AND p.fldMajorVersion = mxPVer.MaxMajorVersion
              join tblFormData FD ON FD.fldFormDataID = p.fldFormDataID
        ),
        workflowTasks as (
            select wf.fldEmitterID as taskEmitterId, task.fldDateCompleted as taskCompleted, task.fldDateUpdated as taskUpdated
              from tblWorkflow wf inner join tblTask task ON task.fldWorkflowID = wf.fldWorkflowID
             where task.fldName like '%Registration%'
        )
             select e.fldEmitterID AS id,
                    e.fldName AS name,
                    e.fldEmitterDisplayId AS emitter_display_id,
                    e.fldNapBenchmarkAllowances AS sop_id,
                    COALESCE(task.taskCompleted, task.taskUpdated, e.fldDateCreated) as accepted_date,
                    inste.fldSiteName as site_name,
                    ca.fldName as competent_authority,
                    'Live' /*es.fldDisplayName*/ AS status,
                    le.fldOperatorID AS legal_entity_id,
                    le.fldName AS legal_entity_name,
                    addr.fldAddressType AS location_type,
                    isnull(f.fldSubmittedXML.value('(fd:formdata/fielddata/Ad_site_main_entrance_grid_reference)[1]', 'NVARCHAR(max)'), '') AS grid_reference,
                    isnull(f.fldSubmittedXML.value('(fd:formdata/fielddata/Ad_date_schedule_1_expected)[1]', 'NVARCHAR(max)'), f.fldSubmittedXML.value('(fd:formdata/fielddata/Ad_date_schedule_1_activity_commence)[1]', 'NVARCHAR(max)')) AS commencement_date,
                    addr.fldAddressLine1 AS location_line_1,
                    addr.fldAddressLine2 AS location_line_2,
                    addr.fldCity AS location_city,
                    addr.fldCountry AS location_country,
                    addr.fldPostcodeZIP AS location_postal_code,
                    addr.fldCoordinateEast AS location_longitude,
                    addr.fldCoordinateNorth AS location_latitude,
                    ver.fldVerifierID AS vb_id,
                    ver.fldName AS vb_name,
                    case when e.fldEmitterDisplayId in ('12122', '12126', '12129', '12135', '13269') then 'EU_ETS_INSTALLATIONS' else 'UK_ETS_INSTALLATIONS' end scheme,
                    inste.fldIsTransferee is_transferee, case when inste.fldIsTransferee = 1 then format(inste.fldTransfereeCode, '000000000') else null end transferee_code
               from tblEmitter e
               join latestPermit f ON e.fldEmitterID = f.fldEmitterID
               join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
               join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID and et.fldName = 'Installation'
               join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID
          left join tblOperator le on le.fldOperatorID = e.fldOperatorID
               join tblInstallationEmitter inste on inste.fldEmitterID = e.fldEmitterID
               join tblAddress addr on addr.fldAddressID = inste.fldAddressID
          left join tblVerifier ver on ver.fldVerifierID = e.fldDefaultVerifierID
          left join workflowTasks task on task.taskEmitterId = e.fldEmitterID
              where e.fldEmitterDisplayId not in (11444,11483,11489,11518,11591,11619,11688,11761,11921,11935,11962,12627,12643,12665,12667,13233,13239,13257,13344,13449,13454,13587,13755,13768,13791,13854,14206)
             """;


    @Override
    public String getResource() {
        return "installation-accounts";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = InstallationAccountHelper.constructQuery(QUERY_BASE, ids);

        List<String> results = new ArrayList<>();
        List<Emitter> emitters = migrationJdbcTemplate.query(query, new EmitterMapper());

        AtomicInteger failedCounter = new AtomicInteger(0);
        for (Emitter emitter: emitters) {
            List<String> migrationResults = doMigrate(emitter, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of accounts results: " + failedCounter + "/" + emitters.size() + " failed");
        return results;
    }

    private List<String> doMigrate(Emitter emitter, AtomicInteger failedCounter) {
        List<String> results = new ArrayList<>();
        String accountId = emitter.getId();

        InstallationAccountDTO accountDTO = accountDTOBuilder.constructInstallationAccountDTO(emitter);

        // validate account
        Set<ConstraintViolation<InstallationAccountDTO>> constraintViolations = validator.validate(accountDTO);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v ->
                    results.add(InstallationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCompetentAuthority(),
                            v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
            failedCounter.incrementAndGet();
            return results;
        }
        
        try {
            // create account
            Long persistedAccountId = installationAccountCreationService.createAccount(accountDTO, emitter);
            
            // appoint vb
           /* Optional.ofNullable(emitter.getVbName())
                .ifPresent(name -> installationAccountVbAppointService.appointVerificationBodyToAccount(persistedAccountId, name));*/
            
            results.add(InstallationAccountHelper.constructSuccessMessage(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCompetentAuthority()));
        } catch (Exception ex) {
            failedCounter.incrementAndGet();
            log.error("migration of installation account: {} failed with {}",
                    emitter.getId(), ExceptionUtils.getRootCause(ex).getMessage());
            results.add(InstallationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCompetentAuthority(),
                    ExceptionUtils.getRootCause(ex).getMessage(), null));
        }
        return results;
    }

}
