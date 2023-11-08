package uk.gov.pmrv.api.migration.workflow;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class MigrationWorkflowService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final AccountRepository accountRepository;
    private final RequestRepository requestRepository;
    private final MigrationRequestMapper requestMapper = Mappers.getMapper(MigrationRequestMapper.class);

    private static final String QUERY_BASE = """
with mig_wf_status as (
    select * from (values
            ('%', 'Approved', 'APPROVED'),
            ('%', 'Cancelled', 'CANCELLED'),
            ('%', 'Deemed Withdrawn', 'WITHDRAWN'),
            ('%', 'Refused', 'REJECTED'),
            ('%', 'Withdrawn', 'WITHDRAWN'),
            ('INPermitApplication', 'Complete', 'APPROVED'),
            ('INNotification', 'Complete', 'APPROVED'),
            ('INSurrender', 'Complete', 'APPROVED'),
            ('INRevocation', 'Complete', 'APPROVED'),
            ('INNewApplicant', 'Complete', 'COMPLETED'),
            ('INBatchReissue', '%', 'COMPLETED'),
            ('INDetermination', 'Complete', 'COMPLETED')
    ) as t (wf_type_etswap, wf_status_etswap, wf_status_pmrv)
), mig_wf as (
    select * from (values
            ('INPermitApplication', 'PERMIT_ISSUANCE'),
            ('INNotification', 'PERMIT_NOTIFICATION'),
            ('INVariation','PERMIT_VARIATION'),
            ('INSurrender', 'PERMIT_SURRENDER'),
            ('INRevocation', 'PERMIT_REVOCATION'),
            ('INTransferPartA','PERMIT_TRANSFER_A'),
            ('INTransferPartB','PERMIT_TRANSFER_B'),
            ('INBatchReissue', 'PERMIT_REISSUE')
    ) as t (wf_type_etswap, wf_type_pmrv)
), emitter as (
    select e.*, et.fldCode emitterType, es.fldDisplayName emitterStatus
      from tblEmitter e
      join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID
      join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID and (es.fldDisplayName = 'Live' or e.fldEmitterID in (select fldEmitterID from mig_emitters_explicitly_live))
     where et.fldName = 'Installation'
), w as (
    select e.fldEmitterId,
           w.fldWorkflowID,
           wt.fldName ETSWAPWorkflowType,
           replace(replace(replace(replace(replace(replace(wtp.fldDisplayFormat,
           '[[EmitterDisplayID]]', format(e.fldEmitterDisplayID, '00000')),
           '[[PhaseDisplayID]]', p.fldDisplayID),
           '[[WorkflowDisplayID]]', w.fldDisplayID),
           '[[ReportingYear]]', isnull(YEAR(rp.fldMonitoringStartDate), '')),
           '[[BatchOperationID]]', isnull(bo.fldDisplayID, '')),
           '[[WorkflowCreatedDate]]', format(w.fldDateCreated, 'dd/MM/yyyy')) WorkflowId,
           w.fldDateCreated, w.fldDateUpdated,
           ws.fldDisplayName ETSWAPWorkflowStatus
      from emitter e
      join tblWorkflow w on w.fldEmitterID = e.fldEmitterID
      join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
      join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID
      join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
      join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID
      left join tblReportingPeriod rp on rp.fldReportingPeriodID = w.fldReportingPeriodID
      left join tblBatchOperationEmitter boe on boe.fldWorkflowID = w.fldWorkflowID
      left join tblBatchOperation bo on bo.fldBatchOperationID = boe.fldBatchOperationID
), v as (
    select w.fldWorkflowID, max(fd.fldMajorVersion) permitVersion
      from w
      join tblTask t           on t.fldWorkflowID = w.fldWorkflowID
      join tblTaskOutcome tout on tout.fldTaskID = t.fldTaskID
      join tblFormSubmissionTaskOutcome fsto on fsto.fldTaskOutcomeID = tout.fldTaskOutcomeID
      join tblFormData fd      on fd.fldFormDataID = fsto.fldFormDataID and fd.fldMinorVersion = 0
      join tblForm f on f.fldFormID = fd.fldFormID
      join tlnkFormTypePhase ftp on ftp.fldFormTypePhaseID = f.fldFormTypePhaseID
      join tlkpFormType ft on ft.fldFormTypeID = ftp.fldFormTypeID and ft.fldName = 'IN_PermitApplication'
      group by w.fldWorkflowID
)
select w.fldEmitterID, w.fldWorkflowID, w.ETSWAPWorkflowType, w.ETSWAPWorkflowStatus, w.WorkflowId, w.fldDateCreated, w.fldDateUpdated, z.wf_type_pmrv, s.wf_status_pmrv, v.permitVersion
  from w
  left join v on v.fldWorkflowID = w.fldWorkflowID
  left join mig_wf_status s on w.ETSWAPWorkflowType like s.wf_type_etswap and w.ETSWAPWorkflowStatus like s.wf_status_etswap
  left join mig_wf z on z.wf_type_etswap = w.ETSWAPWorkflowType
 where w.ETSWAPWorkflowType in ('INPermitApplication','INNotification','INVariation','INTransferPartA','INTransferPartB','INSurrender','INRevocation','INBatchReissue')
   and w.fldDateCreated >= '2021/01/01'
   and wf_status_pmrv is not null
""";

    @Override
    public List<String> migrate(String ids) {
        Map<String, Account> allMigratedAccounts = accountRepository.findByMigratedAccountIdIsNotNull().stream()
                .collect(Collectors.toMap(Account::getMigratedAccountId, acc -> acc));
        List<WorkflowEntityVO> etsWorkflows = queryEts(ids);

        // Convert and save to Request table
        List<Request> requests = requestMapper.toRequests(etsWorkflows, allMigratedAccounts);
        requestRepository.saveAll(requests);

        return ObjectUtils.isEmpty(ids)
                ? List.of()
                : Arrays.stream(ids.split(",")).collect(Collectors.toSet()).stream()
                    .filter(id -> etsWorkflows.stream().noneMatch(w -> w.getEmitterId().equals(id)))
                    .map(id -> "no workflow for account id: " + id)
                    .collect(Collectors.toList());
    }

    private List<WorkflowEntityVO> queryEts(String ids) {
        String sql = ObjectUtils.isEmpty(ids)
                ? QUERY_BASE
                : QUERY_BASE + " and fldEmitterID IN (" +
                    Arrays.stream(ids.split(","))
                            .filter(Objects::nonNull)
                            .map(id -> "'" + id.trim() + "'").collect(Collectors.joining(","))
                    + ")";

        // Get all history workflows from ETSWAP
        return migrationJdbcTemplate.query(sql, new WorkflowEntityVOMapper());
    }

    @Override
    public String getResource() {
        return "workflow";
    }
}
