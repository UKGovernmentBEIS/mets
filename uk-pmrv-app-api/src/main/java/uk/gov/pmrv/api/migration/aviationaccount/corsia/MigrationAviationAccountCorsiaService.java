package uk.gov.pmrv.api.migration.aviationaccount.corsia;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationAccountHelper;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationEmitter;
import uk.gov.pmrv.api.migration.aviationaccount.common.AviationEmitterMapper;
import uk.gov.pmrv.api.migration.aviationaccount.common.MigrationAviationAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationAviationAccountCorsiaService extends MigrationBaseService {

	private final JdbcTemplate migrationJdbcTemplate;
    private final MigrationAviationAccountService migrationAviationAccountService;

    public MigrationAviationAccountCorsiaService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                                 MigrationAviationAccountService migrationAviationAccountService) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.migrationAviationAccountService = migrationAviationAccountService;
    }

    private static final String QUERY_BASE  = """
               with XMLNAMESPACES (
                   'urn:www-toplev-com:officeformsofd' AS fd, 'http://www.w3.org/2001/XMLSchema' as xsd
               ), scopes as (
                   select 'UKETS' scope union select 'CORSIA'
               ), emp1 as (
                   select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID,
                          FD.fldMajorVersion, fd.fldMinorVersion, max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion,
                          FD.fldFormDataID, FD.fldDateUpdated, FD.fldSubmittedXML
                     from tblEmitter e
                     join tblForm F             on f.fldEmitterID = e.fldEmitterID
                     join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0
                     join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
                     join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'
                     join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'
               ), emp2 as (
                   select fldEmitterID, fldEmitterDisplayID, fldFormDataID, fldMajorVersion, fldDateUpdated, fldSubmittedXML,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'NVARCHAR(max)'), '') scopeUkEts,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_corsia_yes)[1]', 'NVARCHAR(max)'), '') scopeCorsia,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Aircraft_operator_legal_status   )[1]', 'NVARCHAR(max)'), '') Aircraft_operator_legal_status,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_company_details_yes   )[1]', 'NVARCHAR(max)'), '') Registered_company_details_yes, /* true|false */
                          -- Company / Limited Liability Partnership - Not Registered_company
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_address_line1         )[1]', 'NVARCHAR(max)'), '') Company_ltd_address_line1        ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_address_line2         )[1]', 'NVARCHAR(max)'), '') Company_ltd_address_line2        ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_city                  )[1]', 'NVARCHAR(max)'), '') Company_ltd_city                 ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_state_province_region )[1]', 'NVARCHAR(max)'), '') Company_ltd_state_province_region,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_postcode_zip          )[1]', 'NVARCHAR(max)'), '') Company_ltd_postcode_zip         ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_country               )[1]', 'NVARCHAR(max)'), '') Company_ltd_country              ,
                          -- Company / Limited Liability Partnership - Registered_company
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_address_line_1        )[1]', 'NVARCHAR(max)'), '') Registered_address_line_1       ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_address_line_2        )[1]', 'NVARCHAR(max)'), '') Registered_address_line_2       ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_city                  )[1]', 'NVARCHAR(max)'), '') Registered_city                 ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_state_province_region )[1]', 'NVARCHAR(max)'), '') Registered_state_province_region,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_postcode_zip          )[1]', 'NVARCHAR(max)'), '') Registered_postcode_zip         ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_country               )[1]', 'NVARCHAR(max)'), '') Registered_country              ,
                          -- Individual / Sole Trader
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_address_line_1        )[1]', 'NVARCHAR(max)'), '') Ao_address_line_1       ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_address_line_2        )[1]', 'NVARCHAR(max)'), '') Ao_address_line_2       ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_city                  )[1]', 'NVARCHAR(max)'), '') Ao_city                 ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_state_province_region )[1]', 'NVARCHAR(max)'), '') Ao_state_province_region,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_postcode_zip          )[1]', 'NVARCHAR(max)'), '') Ao_postcode_zip         ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_country               )[1]', 'NVARCHAR(max)'), '') Ao_country              ,
                          -- Partnership
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Address_line_1        )[1]', 'NVARCHAR(max)'), '') Partner_address_line_1       ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Address_line_2        )[1]', 'NVARCHAR(max)'), '') Partner_address_line_2       ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/City                  )[1]', 'NVARCHAR(max)'), '') Partner_city                 ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/State_province_region )[1]', 'NVARCHAR(max)'), '') Partner_state_province_region,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Postcode_zip          )[1]', 'NVARCHAR(max)'), '') Partner_postcode_zip         ,
                          nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Country               )[1]', 'NVARCHAR(max)'), '') Partner_country             \s
                     from emp1
                    where fldMajorVersion = maxMajorVersion
               ), emp3 as (
                   select emp2.fldEmitterID, emp2.fldEmitterDisplayID, scopeUkEts, scopeCorsia,
                          case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_address_line1
                               when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_address_line_1
                               when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_address_line_1
                               when Aircraft_operator_legal_status = 'Partnership' then Partner_address_line_1
                          end address_line1,
                          case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_address_line2
                               when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_address_line_2
                               when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_address_line_2
                               when Aircraft_operator_legal_status = 'Partnership' then Partner_address_line_2
                          end address_line2,
                          case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_city
                               when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_city
                               when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_city
                               when Aircraft_operator_legal_status = 'Partnership' then Partner_city
                          end city,
                          case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_state_province_region
                               when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_state_province_region
                               when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_state_province_region
                               when Aircraft_operator_legal_status = 'Partnership' then Partner_state_province_region
                          end state_province_region,
                          case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_postcode_zip
                               when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_postcode_zip
                               when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_postcode_zip
                               when Aircraft_operator_legal_status = 'Partnership' then Partner_postcode_zip
                          end postcode_zip,
                          case when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'false' then Company_ltd_country
                               when Aircraft_operator_legal_status = 'Company / Limited Liability Partnership' and Registered_company_details_yes = 'true' then Registered_country
                               when Aircraft_operator_legal_status = 'Individual / Sole Trader' then Ao_country
                               when Aircraft_operator_legal_status = 'Partnership' then Partner_country
                          end country
                     from emp2
               ), repStatus1 as (
                   select esh.fldEmitterID,
                          es.fldDisplayName newStatus,
                          esh.fldDateCreated, max(esh.fldDateCreated) over (partition by esh.fldEmitterID) maxDateCreated,
                          esh.fldReason
                      from tblEmitterStatusHistory esh
                      join tlkpEmitterStatus es on es.fldEmitterStatusID = esh.fldNewEmitterStatusID
               ), repStatus2 as (
                   select * from repStatus1 where fldDateCreated = maxDateCreated
               ), r1 as (
                   select ca.fldName ca,
                          e.fldEmitterID,
                          e.fldEmitterDisplayID,
                          e.fldName,
                          e.fldNapBenchmarkAllowances,
                          e.fldRegistration,
                          ae.fldCrcoCode,
                          ae.fldFirstFlyDate,
                          e.fldDateCreated,
                          'Migration user' createdBy,
                          case when p.fldEmitterID is null then 'NEW' else 'LIVE' end emitterStatus,
                          case es.fldDisplayName when 'Exempt - Non-Commercial' then 'EXEMPT_NON_COMMERCIAL'
                                                 when 'Exempt - Commercial'     then 'EXEMPT_COMMERCIAL'
                                                 else 'REQUIRED_TO_REPORT'
                          end                                         reportingStatus,
                          rs.fldReason                                reportingStatusReason,
                          isnull(rs.fldDateCreated, e.fldDateCreated) reportingStatusSubmissionDate,
                          'Migration user'                            reportingStatusSubmittedBy,
                          v.fldVerifierID vbId,
                          v.fldName vbName,
                          case when p.fldEmitterID is null then 'true' else scopeUkEts end scopeUkEts,
                          isnull(scopeCorsia, 'false') scopeCorsia,
                          address_line1, address_line2, city, state_province_region, postcode_zip, country
                     from tblEmitter e
                     join tblAviationEmitter ae on ae.fldEmitterID = e.fldEmitterID
                     join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID
                     join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID
                     left join emp3 p on p.fldEmitterID = e.fldEmitterID
                     left join repStatus2 rs on rs.fldEmitterID = e.fldEmitterID and newStatus is not null and es.fldDisplayName = newStatus
                     left join tblVerifier v on v.fldVerifierID = e.fldDefaultVerifierID
                    where ae.fldCommissionListName = 'UK ETS'
                      and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')
                )
               select ca,
                      scope,
                      fldEmitterID,
                      case when scopeUkEts = 'true'  and scope = 'UKETS'
                             or scopeUkEts = 'false' and scope = 'CORSIA' then fldEmitterDisplayID
                      else (select c.fldEmitterDisplayID from mig_aviation_emitters c where c.scope = 'CORSIA' and c.fldEmitterID = r1.fldEmitterID)
                      end emitterDisplayID,
                      emitterStatus,
                      fldName,
                      fldNapBenchmarkAllowances,
                      case when scope = 'UKETS' then fldRegistration end fldRegistration,
                      fldCrcoCode, fldFirstFlyDate, fldDateCreated, createdBy,
                      address_line1, address_line2, city, state_province_region, postcode_zip, country,
                      reportingStatus, reportingStatusReason, reportingStatusSubmissionDate, reportingStatusSubmittedBy,
                      vbId, vbName
                 from r1
                cross join scopes
                where (r1.scopeUkEts  = 'true' and scopes.scope = 'UKETS' or r1.scopeCorsia = 'true' and scopes.scope = 'CORSIA')
                  and scopes.scope = 'CORSIA'
            """;


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
