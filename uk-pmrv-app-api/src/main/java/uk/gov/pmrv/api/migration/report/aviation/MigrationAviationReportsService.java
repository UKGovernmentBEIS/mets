package uk.gov.pmrv.api.migration.report.aviation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestCreateService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@Log4j2
public class MigrationAviationReportsService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final AccountRepository accountRepository;
    private final RequestCreateService requestCreateService;
    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    public MigrationAviationReportsService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                           Validator validator,
                                           AccountRepository accountRepository,
                                           RequestCreateService requestCreateService,
                                           AviationReportableEmissionsRepository aviationReportableEmissionsRepository) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.validator = validator;
        this.accountRepository = accountRepository;
        this.requestCreateService = requestCreateService;
        this.aviationReportableEmissionsRepository = aviationReportableEmissionsRepository;
    }

    private static final String QUERY_BASE = """
        with XMLNAMESPACES (
            'urn:www-toplev-com:officeformsofd' AS fd
        ), emitters as (
            select m.scope, m.fldEmitterID, m.fldEmitterDisplayID from mig_aviation_emitters m
        ), wf_aer1 as (
               select e.scope, e.fldEmitterDisplayID, w.fldEmitterID, w.fldWorkflowID wf_aer_id, ws.fldDisplayName wf_aer_status,
                      fd.fldFormDataID, fd.fldMajorVersion, max(fd.fldMajorVersion) over (partition by fd.fldFormID) maxMajorVersion,
                      year(rp.fldMonitoringStartDate) reporting_year,
                      replace(replace(replace(replace(wtp.fldDisplayFormat,
                      '[[EmitterDisplayID]]', format(e.fldEmitterDisplayID, '00000')),
                      '[[PhaseDisplayID]]', p.fldDisplayID),
                      '[[WorkflowDisplayID]]', w.fldDisplayID),
                      '[[ReportingYear]]', isnull(YEAR(rp.fldMonitoringStartDate), '')) wf_aer_identifier
                 from emitters e
                 join tblWorkflow w            on w.fldEmitterID = e.fldEmitterID
                 join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
                 join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID   and wt.fldName = 'AEMReport'
                 join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
                 join tblReportingPeriod rp    on rp.fldReportingPeriodID = w.fldReportingPeriodID
                 join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID
            left join (
                select wf.fldWorkflowID, f.fldFormID
                  from  tlnkWorkflowForm wf
                  join tblForm f                on f.fldFormID = wf.fldFormID
                  join tlnkFormTypePhase ftp    on ftp.fldFormTypePhaseID = f.fldFormTypePhaseID
                  join tlkpFormType ft          on ft.fldFormTypeID = ftp.fldFormTypeID           and ft.fldName = 'AEMReport'
            ) wf on wf.fldWorkflowID = w.fldWorkflowID
            left join tblFormData fd           on fd.fldFormID = wf.fldFormID and fldMinorVersion = 0
        ), wf_aer2 as (
            select * from wf_aer1 where fldMajorVersion = maxMajorVersion or fldFormDataID is null
        ), wf_aer3 as (
                 select wf_aer2.*, fd.fldSubmittedXML,
                        try_parse(nullif(T1.c.value('@value[1]', 'nvarchar(255)'), '') as numeric(20,5) using 'en-gb') wf_aer_approved_reportable_emissions,
                        coalesce(nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vos_opinion)[1]', 'nvarchar(255)'), ''),
                                 nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vos_conclusion_and_sign_off-Opinion_conclusion)[1]', 'nvarchar(255)'), '')
                        ) Vos_opinion,
                        try_parse(nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vcos_corsia_total_co2_emission)[1]', 'nvarchar(255)'), '') as numeric(20,5) using 'en-gb') Vcos_corsia_total_co2_emission,
                        try_parse(nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vcos_verified_corsia_total_co2_emission_chapter3_routes)[1]', 'nvarchar(255)'), '') as numeric(20,5) using 'en-gb') Vcos_verified_corsia_total_co2_emission_chapter3_routes,
                        nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vcos_opinion)[1]', 'nvarchar(255)'), '') Vcos_opinion
                   from wf_aer2
                   join tblWorkflow w  on w.fldWorkflowID  = wf_aer2.wf_aer_id
              left join tblFormData fd on fd.fldFormDataID = wf_aer2.fldFormDataID
            outer apply w.fldWorkflowData.nodes('(workflowData/dataItem[@key="TotalEmissions"])') as T1(c)
        )
        select * into mig_report_history_aviation_aer_1 from wf_aer3;
        
        
        with XMLNAMESPACES (
            'urn:www-toplev-com:officeformsofd' AS fd
        ), wf_aer4 as (
            select scope, wf_aer_id,
                   nullif(trim(T3.c.value('Ded_corsia_total_name[1]', 'nvarchar(max)')), '') Ded_corsia_total_name,
                   try_parse(nullif(trim(T3.c.value('Ded_corsia_total_value[1]', 'nvarchar(max)')), '') as numeric(20,5) using 'en-gb') Ded_corsia_total_value
              from mig_report_history_aviation_aer_1
           outer apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Ded_corsia_totals/row)') as T3(c)
        )
        select a.scope, a.fldEmitterDisplayID, a.fldEmitterID, a.wf_aer_id, a.wf_aer_status, a.reporting_year, a.wf_aer_identifier,
               case when a.scope = 'UKETS' then a.wf_aer_approved_reportable_emissions end wf_aer_approved_reportable_emissions,
               case when a.scope = 'UKETS' then a.Vos_opinion end Vos_opinion,
               case when a.scope = 'CORSIA' then case when a.Vcos_opinion is not null then a.Vcos_corsia_total_co2_emission else t1.Ded_corsia_total_value end end total_emissions_international_flight,
               case when a.scope = 'CORSIA' then case when a.Vcos_opinion is not null then a.Vcos_verified_corsia_total_co2_emission_chapter3_routes else t2.Ded_corsia_total_value end end total_emissions_offsetting_flights,
               case when a.scope = 'CORSIA' then t3.Ded_corsia_total_value end total_emissions_claimed_reductions,
               case when a.scope = 'CORSIA' then a.Vcos_opinion end Vcos_opinion
          into mig_report_history_aviation_aer
          from mig_report_history_aviation_aer_1 a
          left join wf_aer4 t1 on t1.wf_aer_id = a.wf_aer_id and t1.scope = a.scope and t1.Ded_corsia_total_name = 'Total CO2 emissions from international flights (in tonnes)'
          left join wf_aer4 t2 on t2.wf_aer_id = a.wf_aer_id and t2.scope = a.scope and t2.Ded_corsia_total_name = 'Total CO2 emissions from flights subject to offsetting requirements (in tonnes)'
          left join wf_aer4 t3 on t3.wf_aer_id = a.wf_aer_id and t3.scope = a.scope and t3.Ded_corsia_total_name = 'Total emissions reductions claimed from the use of CORSIA eligible fuels (in tonnes)'
         where a.scope = 'UKETS' or a.scope = 'CORSIA' and reporting_year >= 2019;
        
        
        with XMLNAMESPACES (
            'urn:www-toplev-com:officeformsofd' AS fd
        ), emitters as (
            select m.scope, m.fldEmitterID, m.fldEmitterDisplayID from mig_aviation_emitters m
        ), wf_dre1 as (
            select e.scope, e.fldEmitterID,
                   w.fldDateCreated wf_dre_date_created, w.fldDateUpdated wf_dre_date_updated,
                   T1.c.value('@value[1]', 'uniqueidentifier') wf_aer_id,
                   replace(replace(replace(wtp.fldDisplayFormat,
                   '[[EmitterDisplayID]]', format(e.fldEmitterDisplayID, '00000')),
                   '[[PhaseDisplayID]]', p.fldDisplayID),
                   '[[WorkflowDisplayID]]', w.fldDisplayID) wf_dre_identifier
              from emitters e
              join tblWorkflow w            on w.fldEmitterID = e.fldEmitterID
              join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
              join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID   and wt.fldName = 'AVDetermination'
              join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
              join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID and ws.fldDisplayName = 'Complete'
             outer apply w.fldWorkflowData.nodes('(workflowData/dataItem[@key="RelatedWorkflowID"])') as T1(c)
        ), wf_dre2 as (
            select w.*, max(wf_dre_date_updated) over (partition by wf_aer_id) latest_wf_dre_date_updated
              from wf_dre1 w
        ), wf_dre as (
            select * from wf_dre2 where wf_dre_date_updated = latest_wf_dre_date_updated
        )
        select * into mig_report_history_aviation_dre from wf_dre where scope = 'UKETS';
        
        
        with params as (
            select 2012 as mig_reporting_year_start, year(getdate()) - 1 as mig_reporting_year_end
        ), mig_years as (
            select mig_reporting_year_start as reporting_year from params
             union all
            select reporting_year + 1 from mig_years, params
             where reporting_year + 1 <= mig_reporting_year_end
        )
        select a.scope, a.fldEmitterID, a.fldEmitterDisplayID, y.reporting_year, a.wf_aer_identifier, a.wf_aer_status,
               case when a.wf_aer_id is not null then convert(datetime, convert(varchar, y.reporting_year + 1) + '/01/01', 111) else null end wf_aer_date_created,
               wf_aer_approved_reportable_emissions, Vos_opinion,
               b.wf_dre_identifier, b.wf_dre_date_created,
               total_emissions_international_flight, total_emissions_offsetting_flights, total_emissions_claimed_reductions, Vcos_opinion
          from mig_years y
          join mig_report_history_aviation_aer a on a.reporting_year = y.reporting_year
          left join mig_report_history_aviation_dre b on b.fldEmitterID = a.fldEmitterID and b.wf_aer_id = a.wf_aer_id and b.scope = a.scope
         where 1 = 1
         """;

    @Override
    public String getResource() {
        return "aviation-reports";
    }

    @Override
    public List<String> migrate(String ids) {

        migrationJdbcTemplate.execute("""
            drop table if exists mig_report_history_aviation_aer_1;
            drop table if exists mig_report_history_aviation_aer;
            drop table if exists mig_report_history_aviation_dre;
            """);

        final String query = this.constructQuery(QUERY_BASE, ids);
        final List<String> results = new ArrayList<>();
        final List<AviationReport> reports = migrationJdbcTemplate.query(query, new AviationReportMapper());

        final AtomicInteger failedCounter = new AtomicInteger(0);
        for (final AviationReport report : reports) {
            List<String> migrationResults = this.doMigrate(report, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of report requests: " + failedCounter + "/" + reports.size() + " failed");
        return results;
    }

    private String constructQuery(String query, String ids) {

        final StringBuilder queryBuilder = new StringBuilder(query);

        final List<String> idList = !StringUtils.isBlank(ids) ?
            new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();

        if (!idList.isEmpty()) {
            queryBuilder.append(" and a.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }

    private List<String> doMigrate(final AviationReport report, final AtomicInteger failedCounter) {

        final List<String> results = new ArrayList<>();

        final boolean valid = this.validateReport(report, failedCounter, results);
        if (!valid) {
            return results;
        }

        try {
            this.createAer(report, results);
            this.createDre(report, results);
            this.createReportableEmissions(report, results);
        } catch (Exception e) {
            // this should not be reached, as the data has already been validated
            results.add("ERROR: migration of report failed for account " + report.getAccountId() +
                " and year " + report.getReportingYear() + " with " + ExceptionUtils.getRootCause(e).getMessage());
            failedCounter.incrementAndGet();
            log.error("migration of report failed for account {} and year {} with {}",
                report.getAccountId(), report.getReportingYear(), ExceptionUtils.getRootCause(e).getMessage());
        }

        return results;
    }

    private boolean validateReport(final AviationReport report, final AtomicInteger failedCounter,
                                   final List<String> results) {

        // validate data
        final Set<ConstraintViolation<AviationReport>> constraintViolations = validator.validate(report);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v ->
                results.add(
                    "ERROR: " + v.getPropertyPath() + " " + v.getMessage() +
                        " | emitterDisplayId: " + report.getAccountId() +
                        " | year: " + report.getReportingYear()

                ));
            failedCounter.incrementAndGet();
            return false;
        }

        // check that account id exists
        final Long accountId = report.getAccountId();
        if (!accountRepository.existsById(accountId)) {
            results.add("ERROR: Account id does not exist | emitterDisplayId: " + report.getAccountId());
            failedCounter.incrementAndGet();
            return false;
        }

        return true;
    }

    private void createAer(final AviationReport report, final List<String> results) {

        final RequestParams requestParams = RequestParams.builder()
            .type(report.getAccountType().equals(AviationAccountType.UKETS) ? RequestType.AVIATION_AER_UKETS :
                report.getAccountType().equals(AviationAccountType.CORSIA) ? RequestType.AVIATION_AER_CORSIA : null)
            .requestId(report.getAerId())
            .accountId(report.getAccountId())
            .creationDate(report.getAerCreatedDate().atStartOfDay())
            .requestMetadata(AviationAccountType.UKETS.equals(report.getAccountType()) ?
                AviationAerRequestMetadata.builder()
                    .type(RequestMetadataType.AVIATION_AER)
                    .year(report.getReportingYear())
                    .isExempted("Exemption".equals(report.getAerStatus()))
                    .emissions(report.getAerApprovedReportableEmissions())
                    .overallAssessmentType(report.getOverallAssessmentType())
                    .initiatorRequest(AerInitiatorRequest.builder().type(
                        (AviationAccountType.UKETS.equals(report.getAccountType()) ? RequestType.EMP_ISSUANCE_UKETS :
                            (AviationAccountType.CORSIA.equals(report.getAccountType()) ?
                                RequestType.EMP_ISSUANCE_CORSIA : null))
                    ).build())
                    .build() : AviationAccountType.CORSIA.equals(report.getAccountType()) ?
                AviationAerCorsiaRequestMetadata.builder()
                    .type(RequestMetadataType.AVIATION_AER_CORSIA)
                    .year(report.getReportingYear())
                    .isExempted("Exemption".equals(report.getAerStatus()))
                    .initiatorRequest(AerInitiatorRequest.builder().type(
                        (AviationAccountType.UKETS.equals(report.getAccountType()) ? RequestType.EMP_ISSUANCE_UKETS :
                            (AviationAccountType.CORSIA.equals(report.getAccountType()) ?
                                RequestType.EMP_ISSUANCE_CORSIA : null))
                    ).build())
                    .overallAssessmentType(report.getOverallAssessmentType())
                    .emissions(report.getTotalEmissionsInternationalFlight())
                    .totalEmissionsOffsettingFlights(report.getTotalEmissionsOffsettingFlights())
                    .totalEmissionsClaimedReductions(report.getTotalEmissionsClaimedReductions())
                    .build() : null
            )
            .build();

        requestCreateService.createRequest(requestParams, RequestStatus.MIGRATED);

        results.add(
            "emitterDisplayId: " + report.getAccountId() + " | year: " + report.getReportingYear() + " #AVIATION_AER#");
    }

    private void createDre(final AviationReport report, final List<String> results) {

        if (report.getDreId() == null || report.getDreCreatedDate() == null) {
            return;
        }

        final RequestParams requestParams = RequestParams.builder()
            .type(RequestType.AVIATION_DRE_UKETS)
            .requestId(report.getDreId())
            .accountId(report.getAccountId())
            .creationDate(report.getDreCreatedDate().atStartOfDay())
            .requestMetadata(AviationDreRequestMetadata.builder()
                .type(RequestMetadataType.AVIATION_DRE)
                .year(report.getReportingYear())
                .isExempted(
                    "Exemption".equals(report.getAerStatus())
                )
                .build()
            )
            .build();

        requestCreateService.createRequest(requestParams, RequestStatus.MIGRATED);

        results.add(
            "emitterDisplayId: " + report.getAccountId() + " | year: " + report.getReportingYear() + " #AVIATION_DRE#");

    }

    private void createReportableEmissions(final AviationReport report, final List<String> results) {

        if (report.getAerApprovedReportableEmissions() == null && report.getTotalEmissionsInternationalFlight() == null) {
            return;
        }

        final AviationReportableEmissionsEntity reportableEmissionsEntity;

        if (AviationAccountType.CORSIA.equals(report.getAccountType())) {
            reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                    .accountId(report.getAccountId())
                    .year(report.getReportingYear())
                    .reportableEmissions(report.getTotalEmissionsInternationalFlight())
                    .reportableOffsetEmissions(report.getTotalEmissionsOffsettingFlights())
                    .reportableReductionClaimEmissions(report.getTotalEmissionsClaimedReductions())
                    .isFromDre(report.getDreCreatedDate() != null)
                    .isExempted("Exemption".equals(report.getAerStatus()))
                    .build();
        } else {
            reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                    .accountId(report.getAccountId())
                    .year(report.getReportingYear())
                    .reportableEmissions(report.getAerApprovedReportableEmissions())
                    .isFromDre(report.getDreCreatedDate() != null)
                    .isExempted("Exemption".equals(report.getAerStatus()))
                    .build();
        }

        aviationReportableEmissionsRepository.save(reportableEmissionsEntity);

        results.add("emitterDisplayId: " + report.getAccountId() + " | year: " + report.getReportingYear() +
                " #AVIATION REPORTABLE EMISSIONS#");
    }
}
