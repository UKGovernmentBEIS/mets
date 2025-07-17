package uk.gov.pmrv.api.migration.report;

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
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.repository.ReportableEmissionsRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@Log4j2
public class MigrationReportHistoryService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final AccountRepository accountRepository;
    private final RequestCreateService requestCreateService;
    private final ReportableEmissionsRepository reportableEmissionsRepository;

    public MigrationReportHistoryService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                         Validator validator,
                                         AccountRepository accountRepository,
                                         RequestCreateService requestCreateService,
                                         ReportableEmissionsRepository reportableEmissionsRepository) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.validator = validator;
        this.accountRepository = accountRepository;
        this.requestCreateService = requestCreateService;
        this.reportableEmissionsRepository = reportableEmissionsRepository;
    }

    private static final String QUERY_BASE = """
        with XMLNAMESPACES (
            'urn:www-toplev-com:officeformsofd' AS fd
        ), installation_emitters as (
            select e.fldEmitterID, e.fldEmitterDisplayID
              from tblEmitter e
              join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID and et.fldName = 'Installation'
              join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
             where e.fldOperatorID not in ('FD750CD1-7C51-4CA1-B17E-9FBB00FFB979', 'A82C2C76-6E1A-4DB9-A3C9-AE1400C360FA')
               and e.fldEmitterDisplayId not in (14206, 11688)
        ), wf_aer1 as (
            select e.fldEmitterDisplayID, w.fldEmitterID, w.fldWorkflowID wf_aer_id, ws.fldDisplayName wf_aer_status,
                   fd.fldFormDataID, fd.fldMajorVersion, max(fd.fldMajorVersion) over (partition by fd.fldFormID) maxMajorVersion,
                   year(rp.fldMonitoringStartDate) reporting_year,
                   replace(replace(replace(replace(wtp.fldDisplayFormat,
                   '[[EmitterDisplayID]]', format(e.fldEmitterDisplayID, '00000')),
                   '[[PhaseDisplayID]]', p.fldDisplayID),
                   '[[WorkflowDisplayID]]', w.fldDisplayID),
                   '[[ReportingYear]]', isnull(YEAR(rp.fldMonitoringStartDate), '')) wf_aer_identifier
              from installation_emitters e
              join tblWorkflow w            on w.fldEmitterID = e.fldEmitterID
              join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
              join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID   and wt.fldName = 'INAEMReport'
              join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
              join tblReportingPeriod rp    on rp.fldReportingPeriodID = w.fldReportingPeriodID
              join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID
              join tlnkWorkflowForm wf      on wf.fldWorkflowID = w.fldWorkflowID
              join tblForm f                on f.fldFormID = wf.fldFormID
              join tlnkFormTypePhase ftp    on ftp.fldFormTypePhaseID = f.fldFormTypePhaseID
              join tlkpFormType ft          on ft.fldFormTypeID = ftp.fldFormTypeID           and ft.fldName = 'IN_AEMReport'
              join tblFormData fd           on fd.fldFormID = f.fldFormID and fldMinorVersion = 0
        ), wf_aer2 as (
            select * from wf_aer1 where fldMajorVersion = maxMajorVersion
        ), wf_aer as (
            select wf_aer2.*,
                   coalesce(
                       nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vos_opinion)[1]', 'nvarchar(255)'), ''),
                       nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vos_overall_opinion)[1]', 'nvarchar(255)'), '')
                   ) Vos_opinion,
                   cast(nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Es_total_reportable_emissions     )[1]', 'nvarchar(255)'), '') as decimal(20,5)) Es_total_reportable_emissions,
                   cast(nullif(fd.fldSubmittedXML.value('(fd:formdata/fielddata/Vos_total_co2_emission            )[1]', 'nvarchar(255)'), '') as decimal(20,5)) Vos_total_co2_emission,
                   T1.c.value('@value[1]', 'decimal(20,5)') wf_aer_approved_reportable_emissions,
                   T2.c.value('@value[1]', 'nvarchar(255)') reportType
            from wf_aer2
            join tblWorkflow w            on w.fldWorkflowID  = wf_aer2.wf_aer_id
            join tblFormData fd           on fd.fldFormDataID = wf_aer2.fldFormDataID
           outer apply w.fldWorkflowData.nodes('(workflowData/dataItem[@key="TotalEmissions"])') as T1(c)
           outer apply w.fldWorkflowData.nodes('(workflowData/dataItem[@key="ReportType"])') as T2(c)
        )
        select * into mig_report_history_aer from wf_aer;
        
        
        with XMLNAMESPACES (
            'urn:www-toplev-com:officeformsofd' AS fd
        ), installation_emitters as (
            select e.fldEmitterID, e.fldEmitterDisplayID
              from tblEmitter e
              join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID and et.fldName = 'Installation'
              join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
             where e.fldOperatorID not in ('FD750CD1-7C51-4CA1-B17E-9FBB00FFB979', 'A82C2C76-6E1A-4DB9-A3C9-AE1400C360FA')
               and e.fldEmitterDisplayId not in (14206, 11688)
        ), wf_dre1 as (
            select e.fldEmitterID,
                   w.fldDateCreated wf_dre_date_created, w.fldDateUpdated wf_dre_date_updated,
                   T1.c.value('@value[1]', 'uniqueidentifier') wf_aer_id,
                   replace(replace(replace(wtp.fldDisplayFormat,
                   '[[EmitterDisplayID]]', format(e.fldEmitterDisplayID, '00000')),
                   '[[PhaseDisplayID]]', p.fldDisplayID),
                   '[[WorkflowDisplayID]]', w.fldDisplayID) wf_dre_identifier
              from installation_emitters e
              join tblWorkflow w            on w.fldEmitterID = e.fldEmitterID
              join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
              join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID   and wt.fldName = 'INDetermination'
              join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
              join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID and ws.fldDisplayName = 'Complete'
             outer apply w.fldWorkflowData.nodes('(workflowData/dataItem[@key="RelatedWorkflowID"])') as T1(c)
        ), wf_dre2 as (
            select w.*, max(wf_dre_date_updated) over (partition by wf_aer_id) latest_wf_dre_date_updated
              from wf_dre1 w
        ), wf_dre as (
            select * from wf_dre2 where wf_dre_date_updated = latest_wf_dre_date_updated
        )
        select * into mig_report_history_dre from wf_dre;
        
        
        with params as (
            select 2012 as mig_reporting_year_start, year(getdate()) - 1 as mig_reporting_year_end
        ), mig_years as (
            select mig_reporting_year_start as reporting_year from params
             union all
            select reporting_year + 1 from mig_years, params
             where reporting_year + 1 <= mig_reporting_year_end
        )
        select a.fldEmitterID, a.fldEmitterDisplayID, y.reporting_year, wf_aer_identifier, wf_aer_status, reportType,
               case when a.wf_aer_id is not null then convert(datetime, convert(varchar, y.reporting_year + 1) + '/01/01', 111) else null end wf_aer_date_created, a.Vos_opinion,
               b.wf_dre_identifier, b.wf_dre_date_created,
               case when a.wf_aer_approved_reportable_emissions is null and wf_aer_status = 'Review in progress' then
                    case when a.Vos_opinion is not null then Vos_total_co2_emission
                         else Es_total_reportable_emissions
                    end
                    else a.wf_aer_approved_reportable_emissions
               end wf_aer_approved_reportable_emissions
          from mig_years y
          join mig_report_history_aer a on a.reporting_year = y.reporting_year
          left join mig_report_history_dre b on b.fldEmitterID = a.fldEmitterID and b.wf_aer_id = a.wf_aer_id
         where 1 = 1
        """;

    @Override
    public String getResource() {
        return "report-history";
    }

    @Override
    public List<String> migrate(String ids) {

        migrationJdbcTemplate.execute("""
                drop table if exists mig_report_history_aer;
                drop table if exists mig_report_history_dre;
            """);

        final String query = this.constructQuery(QUERY_BASE, ids);
        final List<String> results = new ArrayList<>();
        final List<Report> reports = migrationJdbcTemplate.query(query, new ReportMapper());

        final AtomicInteger failedCounter = new AtomicInteger(0);
        for (final Report report : reports) {
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

    private List<String> doMigrate(final Report report, final AtomicInteger failedCounter) {

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

    private boolean validateReport(final Report report, final AtomicInteger failedCounter, final List<String> results) {

        // validate data
        final Set<ConstraintViolation<Report>> constraintViolations = validator.validate(report);
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

    private void createAer(final Report report, final List<String> results) {

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.AER)
                .requestId(report.getAerId())
                .accountId(report.getAccountId())
                .creationDate(report.getAerCreatedDate().atStartOfDay())
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .year(report.getReportingYear())
                        .overallAssessmentType(report.getOverallAssessmentType())
                        .initiatorRequest(AerInitiatorRequest.builder().type(
                                ("Surrender".equals(report.getReportType()) ? RequestType.PERMIT_SURRENDER :
                                ("Revocation".equals(report.getReportType()) ? RequestType.PERMIT_REVOCATION : RequestType.AER))
                            ).build())
                        .build()
                )
                .build();

        requestCreateService.createRequest(requestParams, RequestStatus.MIGRATED);

        results.add("emitterDisplayId: " + report.getAccountId() + " | year: " + report.getReportingYear() + " #AER#");
    }

    private void createDre(final Report report, final List<String> results) {

        if (report.getDreId() == null || report.getDreCreatedDate() == null) {
            return;
        }

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.DRE)
                .requestId(report.getDreId())
                .accountId(report.getAccountId())
                .creationDate(report.getDreCreatedDate().atStartOfDay())
                .requestMetadata(DreRequestMetadata.builder()
                        .type(RequestMetadataType.DRE)
                        .year(report.getReportingYear())
                        .build()
                )
                .build();

        requestCreateService.createRequest(requestParams, RequestStatus.MIGRATED);

        results.add("emitterDisplayId: " + report.getAccountId() + " | year: " + report.getReportingYear() + " #DRE#");

    }

    private void createReportableEmissions(final Report report, final List<String> results) {

        if (report.getAerApprovedReportableEmissions() == null) {
            return;
        }

        final ReportableEmissionsEntity reportableEmissionsEntity = ReportableEmissionsEntity.builder()
                .accountId(report.getAccountId())
                .year(report.getReportingYear())
                .reportableEmissions(report.getAerApprovedReportableEmissions())
                .isFromDre(report.getDreCreatedDate() != null)
                .build();

        reportableEmissionsRepository.save(reportableEmissionsEntity);

        results.add("emitterDisplayId: " + report.getAccountId() + " | year: " + report.getReportingYear() + " #REPORTABLE EMISSIONS#");
    }
}
