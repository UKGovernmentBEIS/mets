package uk.gov.pmrv.api.migration.aviationaccount.corsia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationAccountHelper;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationEmitter;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationEmitterMapper;
import uk.gov.pmrv.api.migration.aviationaccount.common.MigrationAviationAccountService;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationAviationAccountCorsiaService extends MigrationBaseService {

	private final JdbcTemplate migrationJdbcTemplate;
    private final MigrationAviationAccountService migrationAviationAccountService;

    private static final String QUERY_BASE  = "with XMLNAMESPACES (\r\n"
    		+ "    'urn:www-toplev-com:officeformsofd' AS fd, 'http://www.w3.org/2001/XMLSchema' as xsd\r\n"
    		+ "), scopes as (\r\n"
    		+ "    select 'UKETS' scope union select 'CORSIA'\r\n"
    		+ "), emp1 as (\r\n"
    		+ "    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID,\r\n"
    		+ "           FD.fldMajorVersion, fd.fldMinorVersion, max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion,\r\n"
    		+ "           FD.fldFormDataID, FD.fldDateUpdated, FD.fldSubmittedXML\r\n"
    		+ "      from tblEmitter e\r\n"
    		+ "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\r\n"
    		+ "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\r\n"
    		+ "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n"
    		+ "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\r\n"
    		+ "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\r\n"
    		+ "), emp2 as (\r\n"
    		+ "    select fldEmitterID, fldEmitterDisplayID, fldFormDataID, fldMajorVersion, fldDateUpdated, fldSubmittedXML,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'NVARCHAR(max)'), '') scopeUkEts,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_corsia_yes)[1]', 'NVARCHAR(max)'), '') scopeCorsia,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aircraft_operator_legal_status   )[1]', 'NVARCHAR(max)'), '') Aircraft_operator_legal_status,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_company_details_yes   )[1]', 'NVARCHAR(max)'), '') Registered_company_details_yes, /* true|false */\r\n"
    		+ "           -- Company / Limited Liability Partnership - Not Registered_company\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_address_line1        )[1]', 'NVARCHAR(max)'), '') Company_ltd_address_line1        ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_address_line2        )[1]', 'NVARCHAR(max)'), '') Company_ltd_address_line2        ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_city                 )[1]', 'NVARCHAR(max)'), '') Company_ltd_city                 ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_state_province_region)[1]', 'NVARCHAR(max)'), '') Company_ltd_state_province_region,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_postcode_zip         )[1]', 'NVARCHAR(max)'), '') Company_ltd_postcode_zip         ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_country              )[1]', 'NVARCHAR(max)'), '') Company_ltd_country              ,\r\n"
    		+ "           -- Company / Limited Liability Partnership - Registered_company\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_address_line_1        )[1]', 'NVARCHAR(max)'), '') Registered_address_line_1       ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_address_line_2        )[1]', 'NVARCHAR(max)'), '') Registered_address_line_2       ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_city                  )[1]', 'NVARCHAR(max)'), '') Registered_city                 ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_state_province_region )[1]', 'NVARCHAR(max)'), '') Registered_state_province_region,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_postcode_zip          )[1]', 'NVARCHAR(max)'), '') Registered_postcode_zip         ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_country               )[1]', 'NVARCHAR(max)'), '') Registered_country              ,\r\n"
    		+ "           -- Individual / Sole Trader\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_address_line_1                )[1]', 'NVARCHAR(max)'), '') Ao_address_line_1       ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_address_line_2                )[1]', 'NVARCHAR(max)'), '') Ao_address_line_2       ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_city                          )[1]', 'NVARCHAR(max)'), '') Ao_city                 ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_state_province_region         )[1]', 'NVARCHAR(max)'), '') Ao_state_province_region,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_postcode_zip                  )[1]', 'NVARCHAR(max)'), '') Ao_postcode_zip         ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_country                       )[1]', 'NVARCHAR(max)'), '') Ao_country              ,\r\n"
    		+ "           -- Partnership\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_address_line_1           )[1]', 'NVARCHAR(max)'), '') Partner_address_line_1       ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_address_line_2           )[1]', 'NVARCHAR(max)'), '') Partner_address_line_2       ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_city                     )[1]', 'NVARCHAR(max)'), '') Partner_city                 ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_state_province_region    )[1]', 'NVARCHAR(max)'), '') Partner_state_province_region,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_postcode_zip             )[1]', 'NVARCHAR(max)'), '') Partner_postcode_zip         ,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_country                  )[1]', 'NVARCHAR(max)'), '') Partner_country              \r\n"
    		+ "      from emp1\r\n"
    		+ "     where fldMajorVersion = maxMajorVersion\r\n"
    		+ "), emp3 as (\r\n"
    		+ "    select emp2.fldEmitterID, emp2.fldEmitterDisplayID, scopeUkEts, scopeCorsia,\r\n"
    		+ "           case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_address_line1\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_address_line_1\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_address_line_1\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Partnership' then Partner_address_line_1\r\n"
    		+ "           end address_line1,\r\n"
    		+ "           case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_address_line2\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_address_line_2\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_address_line_2\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Partnership' then Partner_address_line_2\r\n"
    		+ "           end address_line2,\r\n"
    		+ "           case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_city\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_city\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_city\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Partnership' then Partner_city\r\n"
    		+ "           end city,\r\n"
    		+ "           case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_state_province_region\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_state_province_region\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_state_province_region\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Partnership' then Partner_state_province_region\r\n"
    		+ "           end state_province_region,\r\n"
    		+ "           case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_postcode_zip\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_postcode_zip\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_postcode_zip\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Partnership' then Partner_postcode_zip\r\n"
    		+ "           end postcode_zip,\r\n"
    		+ "           case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_country\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_country\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_country\r\n"
    		+ "                when Aircraft_operator_legal_status = 'Partnership' then Partner_country\r\n"
    		+ "           end country\r\n"
    		+ "      from emp2\r\n"
    		+ "), repStatus1 as (\r\n"
    		+ "    select esh.fldEmitterID,\r\n"
    		+ "           es.fldDisplayName newStatus,\r\n"
    		+ "           esh.fldDateCreated, max(esh.fldDateCreated) over (partition by esh.fldEmitterID) maxDateCreated,\r\n"
    		+ "           esh.fldReason\r\n"
    		+ "       from tblEmitterStatusHistory esh\r\n"
    		+ "       join tlkpEmitterStatus es on es.fldEmitterStatusID = esh.fldNewEmitterStatusID\r\n"
    		+ "), repStatus2 as (\r\n"
    		+ "    select * from repStatus1 where fldDateCreated = maxDateCreated\r\n"
    		+ "), r1 as (\r\n"
    		+ "    select ca.fldName ca,\r\n"
    		+ "           e.fldEmitterID,\r\n"
    		+ "           e.fldEmitterDisplayID,\r\n"
    		+ "           e.fldName,\r\n"
    		+ "           e.fldNapBenchmarkAllowances,\r\n"
    		+ "           e.fldRegistration,\r\n"
    		+ "           ae.fldCrcoCode,\r\n"
    		+ "           ae.fldFirstFlyDate,\r\n"
    		+ "           e.fldDateCreated,\r\n"
    		+ "           'Migration user' createdBy,\r\n"
    		+ "           case when p.fldEmitterID is null then 'NEW' else 'LIVE' end emitterStatus,\r\n"
    		+ "           case es.fldDisplayName when 'Exempt - Non-Commercial' then 'EXEMPT_NON_COMMERCIAL'\r\n"
    		+ "                                  when 'Exempt - Commercial'     then 'EXEMPT_COMMERCIAL'\r\n"
    		+ "                                  else 'REQUIRED_TO_REPORT'\r\n"
    		+ "           end                                         reportingStatus,\r\n"
    		+ "           rs.fldReason                                reportingStatusReason,\r\n"
    		+ "           isnull(rs.fldDateCreated, e.fldDateCreated) reportingStatusSubmissionDate,\r\n"
    		+ "           'Migration user'                            reportingStatusSubmittedBy,\r\n"
    		+ "           v.fldVerifierID vbId,\r\n"
    		+ "           v.fldName vbName,\r\n"
    		+ "           case when p.fldEmitterID is null then 'true' else scopeUkEts end scopeUkEts,\r\n"
    		+ "           isnull(scopeCorsia, 'false') scopeCorsia,\r\n"
    		+ "           address_line1, address_line2, city, state_province_region, postcode_zip, country\r\n"
    		+ "      from tblEmitter e\r\n"
    		+ "      join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID\r\n"
    		+ "      join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID\r\n"
    		+ "      join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID\r\n"
    		+ "      left join emp3 p on p.fldEmitterID = e.fldEmitterID\r\n"
    		+ "      left join repStatus2 rs on rs.fldEmitterID = e.fldEmitterID and newStatus is not null and es.fldDisplayName = newStatus\r\n"
    		+ "      left join tblVerifier v on v.fldVerifierID = e.fldDefaultVerifierID\r\n"
    		+ "     where ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "       and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ " )\r\n"
    		+ "select ca,\r\n"
    		+ "       scope,\r\n"
    		+ "       fldEmitterID,\r\n"
    		+ "       case when scopeUkEts = 'true'  and scope = 'UKETS' \r\n"
    		+ "              or scopeUkEts = 'false' and scope = 'CORSIA' then fldEmitterDisplayID\r\n"
    		+ "       else (select c.fldEmitterDisplayID from mig_aviation_emitters c where c.scope = 'CORSIA' and c.fldEmitterID = r1.fldEmitterID)\r\n"
    		+ "       end emitterDisplayID,\r\n"
    		+ "       emitterStatus,\r\n"
    		+ "       fldName,\r\n"
    		+ "       fldNapBenchmarkAllowances,\r\n"
    		+ "       case when scope = 'UKETS' then fldRegistration end fldRegistration,\r\n"
    		+ "       fldCrcoCode, fldFirstFlyDate, fldDateCreated, createdBy,\r\n"
    		+ "       address_line1, address_line2, city, state_province_region, postcode_zip, country,\r\n"
    		+ "       reportingStatus, reportingStatusReason, reportingStatusSubmissionDate, reportingStatusSubmittedBy,\r\n"
    		+ "       vbId, vbName\r\n"
    		+ "  from r1\r\n"
    		+ " cross join scopes\r\n"
    		+ " where (r1.scopeUkEts  = 'true' and scopes.scope = 'UKETS' or r1.scopeCorsia = 'true' and scopes.scope = 'CORSIA')\r\n"
    		+ "   and scopes.scope = 'CORSIA'";


    @Override
    public String getResource() {
        return "aviation-accounts-corsia";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = AviationAccountHelper.constructQuery(QUERY_BASE, ids);

        List<String> results = new ArrayList<>();
        List<AviationEmitter> emitters = migrationJdbcTemplate.query(query, new AviationEmitterMapper());

        AtomicInteger failedCounter = new AtomicInteger(0);
        for (AviationEmitter emitter: emitters) {
            List<String> migrationResults = migrationAviationAccountService.doMigrate(
            		emitter, failedCounter, EmissionTradingScheme.CORSIA);
            results.addAll(migrationResults);
        }

        results.add("migration of aviation accounts corsia results: " + failedCounter + "/" + emitters.size() + " failed");
        return results;
    }
}
