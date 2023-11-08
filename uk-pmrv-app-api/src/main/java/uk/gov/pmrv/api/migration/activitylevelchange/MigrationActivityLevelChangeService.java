package uk.gov.pmrv.api.migration.activitylevelchange;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.allowance.domain.AllowanceActivityLevelEntity;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.mapper.AllowanceMapper;
import uk.gov.pmrv.api.allowance.repository.AllowanceActivityLevelRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationActivityLevelChangeService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final AllowanceActivityLevelRepository allowanceActivityLevelRepository;
    private final AccountRepository accountRepository;
    private final Validator validator;
    private final MigrationActivityLevelChangeMapper migrationActivityLevelChangeMapper = Mappers.getMapper(MigrationActivityLevelChangeMapper.class);
    private static final AllowanceMapper ALLOWANCE_MAPPER = Mappers.getMapper(AllowanceMapper.class);

    private static final String QUERY_BASE = """
        with XMLNAMESPACES (
        	'urn:www-toplev-com:officeformsofd' AS fd
        ), a as (
            select e.fldEmitterID, e.fldEmitterDisplayID,
                   fd.fldMajorVersion * 1000 + fd.fldMinorVersion formVersion,
                   max(fd.fldMajorVersion * 1000 + fd.fldMinorVersion) over (partition by f.fldFormID) maxFormVersion,
        	       year(w.fldDateCreated) workflowYear, ws.fldDisplayName workflowStatus,
                   w.fldDateUpdated workflowDateCompleted, w.fldDisplayID, fd.fldSubmittedXML
              from tblEmitter e
              join tblForm f                on f.fldEmitterID = e.fldEmitterID
              join tblFormData fd           on fd.fldFormID = F.fldFormID
              join tlnkFormTypePhase FTP    on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID
              join tlkpFormType FT          on FTP.fldFormTypeID = FT.fldFormTypeID
              join tlkpPhase P              on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayID >=3
              join tlnkWorkflowForm wf      on wf.fldFormID = f.fldFormID
              join tblWorkflow w            on w.fldWorkflowID = wf.fldWorkflowID
              join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID  and ws.fldDisplayName = 'Complete'
              join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
              join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID    and wt.fldName = 'INPartialCessation'
              join tlkpEmitterType et       on et.fldEmitterTypeID = wt.fldEmitterTypeID       and et.fldName = 'Installation'
        )
        select a.fldEmitterID as emitterId, a.fldEmitterDisplayID as emitterDisplayId,
        workflowYear as fldDateCreated, workflowDateCompleted as fldDateUpdated,
               nullif(trim(T.c.value('(Tr2_history_year         )[1]', 'nvarchar(max)')), '') history_year,
               nullif(trim(T.c.value('(Tr2_subinstallation_name )[1]', 'nvarchar(max)')), '') subinstallation_name,
               nullif(trim(T.c.value('(Tr2_change_type          )[1]', 'nvarchar(max)')), '') change_type,
               nullif(trim(T.c.value('(Tr2_change_activity_level)[1]', 'nvarchar(max)')), '') change_activity_level,
               nullif(trim(T.c.value('(Tr2_comments             )[1]', 'nvarchar(max)')), '') comments
          from a
         cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Tr2_actvity_changes_in_current_year/row)') as T(c)
         where formVersion = maxFormVersion
           and workflowYear >= 2021
        """;


    @Override
    public String getResource() {
        return "activity-level-change";
    }

    @Override
    public List<String> migrate(String ids) {
        List<String> results = new ArrayList<>();
        AtomicInteger failedCounter = new AtomicInteger(0);

        String query = constructQuery(QUERY_BASE, ids);
        List<ActivityLevelChangeVO> activityLevelChangeVOS = migrationJdbcTemplate.query(
            query,
            new ActivityLevelChangeVOMapper()
        );
        List<AllowanceActivityLevelEntity> allowanceActivityLevelEntities = new ArrayList<>();
        for (final ActivityLevelChangeVO activityLevelChangeVO : activityLevelChangeVOS) {
            if (accountRepository.findByMigratedAccountId(activityLevelChangeVO.getEmitterId()).isEmpty()) {
                results.add(
                    constructErrorMessage(
                        activityLevelChangeVO,
                        "emitterId not found",
                        activityLevelChangeVO.getEmitterId()
                    )
                );
                failedCounter.incrementAndGet();
                continue;
            }
            List<String> constraintViolations = validator.validate(activityLevelChangeVO).stream()
                .map(constraint -> constructErrorMessage(activityLevelChangeVO,
                    constraint.getMessage(), constraint.getPropertyPath().iterator().next().getName()
                        + ":" + constraint.getInvalidValue())).toList();
            results.addAll(constraintViolations);
            if (constraintViolations.isEmpty()) {
                HistoricalActivityLevel historicalActivityLevel =
                    migrationActivityLevelChangeMapper.toActivityLevel(activityLevelChangeVO);
                AllowanceActivityLevelEntity activityLevelEntity =
                    ALLOWANCE_MAPPER.toActivityLevelEntity(
                        historicalActivityLevel,
                        Long.parseLong(activityLevelChangeVO.getEmitterDisplayId())
                    );
                allowanceActivityLevelEntities.add(activityLevelEntity);
            } else {
                failedCounter.incrementAndGet();
            }
        }

        allowanceActivityLevelRepository.saveAll(allowanceActivityLevelEntities);

        results.add("migration of activity level changes results: " + failedCounter.get() + "/"
            + activityLevelChangeVOS.size() + " failed");
        return results;
    }

    private String constructErrorMessage(ActivityLevelChangeVO activityLevelChangeVO, String errorMessage, String data) {
        return " | emitterId: " + activityLevelChangeVO.getEmitterId() +
            " | emitterDisplayId: " + activityLevelChangeVO.getEmitterDisplayId() +
            " | Error: " + errorMessage +
            " | data: " + data;
    }

    private String constructQuery(String query, String ids) {
        StringBuilder queryBuilder = new StringBuilder(query);

        List<String> idList =
            !StringUtils.isBlank(ids) ? new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();
        if (!idList.isEmpty()) {
            queryBuilder.append(" and a.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }
}
