package uk.gov.pmrv.api.migration.notes.request.aviation.ukets;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.note.NotePayload;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.notes.domain.FileNote;
import uk.gov.netz.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.notes.request.RequestNoteFileMapper;
import uk.gov.pmrv.api.migration.notes.request.RequestNoteFileRow;
import uk.gov.pmrv.api.migration.notes.request.RequestNoteMapper;
import uk.gov.pmrv.api.migration.notes.request.RequestNoteRow;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestNoteRepository;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Log4j2
public class RequestNoteUkEtsMigrationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final RequestRepository requestRepository;
    private final RequestNoteRepository requestNoteRepository;
    private final FileNoteRepository fileNoteRepository;

    public RequestNoteUkEtsMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                            RequestRepository requestRepository,
                                            RequestNoteRepository requestNoteRepository,
                                            FileNoteRepository fileNoteRepository) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.requestRepository = requestRepository;
        this.requestNoteRepository = requestNoteRepository;
        this.fileNoteRepository = fileNoteRepository;
    }

    private static final String WORKFLOW_NOTE_QUERY_BASE =
        """
            with em as (
                select e.fldEmitterID, e.fldEmitterDisplayID, ca.fldName caName
                  from tblEmitter e
                  join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID
            ), notes as (
                select caName, em.fldEmitterDisplayID, n.*
                  from em
                  join tblNote n on n.fldEmitterID = em.fldEmitterID and n.fldDatePublished is not null
                 where n.fldWorkflowID is not null
            ), workflow as (
                select n.fldNoteID,
                       replace(replace(replace(replace(replace(replace(wtp.fldDisplayFormat,
                       '[[EmitterDisplayID]]', format(n.fldEmitterDisplayID, '00000')),
                       '[[PhaseDisplayID]]', p.fldDisplayID),
                       '[[WorkflowDisplayID]]', w.fldDisplayID),
                       '[[ReportingYear]]', isnull(YEAR(rp.fldMonitoringStartDate), '')),
                       '[[BatchOperationID]]', isnull(bo.fldDisplayID, '')),
                       '[[WorkflowCreatedDate]]', format(w.fldDateCreated, 'dd/MM/yyyy')) workflowId
                  from notes n
                  join tblWorkflow w on w.fldWorkflowID = n.fldWorkflowID
                  join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
                  join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID
                  join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
                  join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID
                  left join tblReportingPeriod rp on rp.fldReportingPeriodID = w.fldReportingPeriodID
                  left join tblBatchOperationEmitter boe on boe.fldWorkflowID = w.fldWorkflowID
                  left join tblBatchOperation bo on bo.fldBatchOperationID = boe.fldBatchOperationID
            )
            select n.caName, n.fldEmitterID, n.fldEmitterDisplayID, n.fldNoteID, n.fldDateCreated, c.fldName + ' ' + c.fldSurname userFullName, n.fldBody, w.workflowId
             from notes n
              join tblAvatar a on a.fldAvatarID = n.fldCreatedByAvatarID
              join tblCompetentAuthorityUser cau on cau.fldUserID = a.fldUserID
              join tblUser u on u.fldUserID = cau.fldUserID
              join tblContact c on c.fldContactID = u.fldContactID
              join workflow w on w.fldNoteID = n.fldNoteID
              """;

    private static final String WORKFLOW_NOTE_FILE_QUERY_BASE =
        """
            with em as (
                select e.fldEmitterID, e.fldEmitterDisplayID, ca.fldName caName
                  from tblEmitter e
                  join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID
            ), notes as (
                select caName, em.fldEmitterDisplayID, n.*
                  from em
                  join tblNote n on n.fldEmitterID = em.fldEmitterID and n.fldDatePublished is not null
                 where n.fldWorkflowID is not null
            ), workflow as (
                select n.fldNoteID,
                       replace(replace(replace(replace(replace(replace(wtp.fldDisplayFormat,
                       '[[EmitterDisplayID]]', format(n.fldEmitterDisplayID, '00000')),
                       '[[PhaseDisplayID]]', p.fldDisplayID),
                       '[[WorkflowDisplayID]]', w.fldDisplayID),
                       '[[ReportingYear]]', isnull(YEAR(rp.fldMonitoringStartDate), '')),
                       '[[BatchOperationID]]', isnull(bo.fldDisplayID, '')),
                       '[[WorkflowCreatedDate]]', format(w.fldDateCreated, 'dd/MM/yyyy')) workflowId
                  from notes n
                  join tblWorkflow w on w.fldWorkflowID = n.fldWorkflowID
                  join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID
                  join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID
                  join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID
                  join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID
                  left join tblReportingPeriod rp on rp.fldReportingPeriodID = w.fldReportingPeriodID
                  left join tblBatchOperationEmitter boe on boe.fldWorkflowID = w.fldWorkflowID
                  left join tblBatchOperation bo on bo.fldBatchOperationID = boe.fldBatchOperationID
            )
            select n.caName, n.fldEmitterID, n.fldEmitterDisplayID, n.fldNoteID, na.fldNoteAttachmentID, a.fldFileLocation, a.fldFileName, w.workflowId
              from notes n
              join workflow w on w.fldNoteID = n.fldNoteID
              join tlnkNoteAttachment na on na.fldNoteID = n.fldNoteID
              join tblAttachment a on a.fldAttachmentID = na.fldAttachmentID
              """;

    @Override
    public String getResource() {
        return "workflow-aviation-ukets-notes";
    }

    @Override
    public List<String> migrate(String ids) {

        final List<String> failed = new ArrayList<>();

        final String noteQuery = this.constructQuery(WORKFLOW_NOTE_QUERY_BASE, ids);
        final List<RequestNoteRow> requestNoteRows = migrationJdbcTemplate.query(noteQuery, new RequestNoteMapper());

        final String fileQuery = this.constructQuery(WORKFLOW_NOTE_FILE_QUERY_BASE, ids);
        final List<RequestNoteFileRow> requestNoteFileRows =
            migrationJdbcTemplate.query(fileQuery, new RequestNoteFileMapper())
                .stream()
                .filter(f -> MigrationConstants.ALLOWED_FILE_TYPES.contains(
                    f.getFileName().substring(f.getFileName().lastIndexOf(".")).toLowerCase()))
                .toList();

        for (final RequestNoteRow requestNoteRow : requestNoteRows) {
            try {

                final List<RequestNoteFileRow> files = requestNoteFileRows.stream()
                    .filter(f -> f.getNoteId().equals(requestNoteRow.getNoteId()))
                    .toList();
                final List<String> errors = this.createWorkflowNote(requestNoteRow, files);
                failed.addAll(errors);

            } catch (Exception ex) {

                final String error = "Workflow Id: " + requestNoteRow.getRequestId()
                    + ", Note Id:  " + requestNoteRow.getNoteId()
                    + " | Error: "
                    + ExceptionUtils.getRootCause(ex).getMessage();

                log.error(error);
                failed.add(error);
            }
        }
        failed.add("Statistics: Total: " + requestNoteRows.size() + ". Failed: " + failed.size());

        return failed;
    }

    private List<String> createWorkflowNote(final RequestNoteRow requestNoteRow,
                                            final List<RequestNoteFileRow> files) {

        final String workflowId = requestNoteRow.getRequestId();
        final Optional<Request> migratedWorkflow = requestRepository.findById(workflowId);

        if (migratedWorkflow.isEmpty()) {

            final String error = "Workflow Id: " + requestNoteRow.getRequestId()
                + ", Note Id:  " + requestNoteRow.getNoteId()
                + " | Error: Workflow does not exist";

            log.warn(error);
            return List.of(error);

        } else if (RequestType.AVIATION_AER_UKETS.equals(migratedWorkflow.get().getType()) ||
            RequestType.AVIATION_DRE_UKETS.equals(migratedWorkflow.get().getType())) {

            final Map<UUID, String> filesMap = files.stream()
                .collect(Collectors.toMap(RequestNoteFileRow::getAttachmentId, RequestNoteFileRow::getFileName));

            final RequestNote workflowNote = RequestNote.builder()
                .requestId(workflowId)
                .payload(NotePayload.builder().note(requestNoteRow.getPayload()).files(filesMap).build())
                .lastUpdatedOn(requestNoteRow.getDateCreated())
                .submitter(requestNoteRow.getSubmitter())
                .submitterId(MigrationConstants.MIGRATION_PROCESS_USER)
                .build();

            requestNoteRepository.save(workflowNote);

            for (final RequestNoteFileRow file : files) {

                final FileNote fileNote = FileNote.builder()
                    .requestId(file.getRequestId())
                    .status(FileStatus.PENDING_MIGRATION)
                    .uuid(file.getAttachmentId().toString())
                    .fileName(file.getFileName())
                    .fileContent(file.getFileContent().getBytes())
                    .fileSize(file.getFileContent().getBytes().length)
                    .fileType(org.springframework.util.StringUtils.getFilenameExtension(file.getFileName()))
                    .createdBy(MigrationConstants.MIGRATION_PROCESS_USER)
                    .build();

                fileNoteRepository.save(fileNote);
            }
        }
        return List.of();
    }

    private String constructQuery(final String query, final String ids) {

        final StringBuilder queryBuilder = new StringBuilder(query);

        final List<String> idList =
            !StringUtils.isBlank(ids) ? new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();
        if (!idList.isEmpty()) {
            queryBuilder.append(" where n.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        queryBuilder.append(";");
        return queryBuilder.toString();
    }
}
