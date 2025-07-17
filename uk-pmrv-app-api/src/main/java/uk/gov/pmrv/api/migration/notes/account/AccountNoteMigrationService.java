package uk.gov.pmrv.api.migration.notes.account;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.note.NotePayload;
import uk.gov.netz.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.AccountNote;
import uk.gov.pmrv.api.account.repository.AccountNoteRepository;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.notes.domain.FileNote;
import uk.gov.netz.api.files.notes.repository.FileNoteRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

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
public class AccountNoteMigrationService extends MigrationBaseService {

    private static final String ACCOUNT_NOTE_QUERY_BASE =
        "with em as (\n" +
            "    select e.fldEmitterID, e.fldEmitterDisplayID, ca.fldName caName\n" +
            "      from tblEmitter e\n" +
            "      join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID\n" +
            "), notes as (\n" +
            "    select caName, em.fldEmitterDisplayID, n.*\n" +
            "      from em\n" +
            "      join tblNote n on n.fldEmitterID = em.fldEmitterID and n.fldDatePublished is not null\n" +
            "     where n.fldWorkflowID is null\n" +
            ")\n" +
            "select n.caName, n.fldEmitterID, n.fldEmitterDisplayID, n.fldNoteID, n.fldDateCreated, c.fldName + ' ' + c.fldSurname userFullName, n.fldBody\n" +
            "  from notes n\n" +
            "  join tblAvatar a on a.fldAvatarID = n.fldCreatedByAvatarID\n" +
            "  join tblCompetentAuthorityUser cau on cau.fldUserID = a.fldUserID\n" +
            "  join tblUser u on u.fldUserID = cau.fldUserID\n" +
            "  join tblContact c on c.fldContactID = u.fldContactID\n";

    private static final String ACCOUNT_NOTE_FILE_QUERY_BASE =
        "with em as (\n" +
            "    select e.fldEmitterID, e.fldEmitterDisplayID, ca.fldName caName\n" +
            "      from tblEmitter e\n" +
            "      join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID\n" +
            "), notes as (\n" +
            "    select caName, em.fldEmitterDisplayID, n.*\n" +
            "      from em\n" +
            "      join tblNote n on n.fldEmitterID = em.fldEmitterID and n.fldDatePublished is not null\n" +
            "     where n.fldWorkflowID is null -- for account notes\n" +
            " -- where n.fldWorkflowID is not null -- for workflow notes\n" +
            ")\n" +
            "select n.caName, n.fldEmitterID, n.fldEmitterDisplayID, n.fldNoteID, na.fldNoteAttachmentID, a.fldFileLocation, a.fldFileName\n" +
            "  from notes n\n" +
            "  join tlnkNoteAttachment na on na.fldNoteID = n.fldNoteID\n" +
            "  join tblAttachment a on a.fldAttachmentID = na.fldAttachmentID";

    private final JdbcTemplate migrationJdbcTemplate;
    private final AccountRepository accountRepository;
    private final AccountNoteRepository accountNoteRepository;
    private final FileNoteRepository fileNoteRepository;

    public AccountNoteMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                       AccountRepository accountRepository,
                                       AccountNoteRepository accountNoteRepository,
                                       FileNoteRepository fileNoteRepository) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.accountRepository = accountRepository;
        this.accountNoteRepository = accountNoteRepository;
        this.fileNoteRepository = fileNoteRepository;
    }

    @Override
    public String getResource() {
        return "account-notes";
    }

    @Override
    public List<String> migrate(String ids) {

        final List<String> failed = new ArrayList<>();

        final String noteQuery = this.constructQuery(ACCOUNT_NOTE_QUERY_BASE, ids);
        final List<AccountNoteRow> accountNoteRows = migrationJdbcTemplate.query(noteQuery, new AccountNoteMapper());

        final String fileQuery = this.constructQuery(ACCOUNT_NOTE_FILE_QUERY_BASE, ids);
        final List<AccountNoteFileRow> accountNoteFileRows =
            migrationJdbcTemplate.query(fileQuery, new AccountNoteFileMapper())
                .stream()
                .filter(f -> MigrationConstants.ALLOWED_FILE_TYPES.contains(
                    f.getFileName().substring(f.getFileName().lastIndexOf(".")).toLowerCase()))
                .toList();

        for (final AccountNoteRow accountNoteRow : accountNoteRows) {
            try {

                final List<AccountNoteFileRow> files = accountNoteFileRows.stream()
                    .filter(f -> f.getNoteId().equals(accountNoteRow.getNoteId()))
                    .toList();
                final List<String> errors = this.createAccountNote(accountNoteRow, files);
                failed.addAll(errors);

            } catch (Exception ex) {

                final String error = "Account Id: " + accountNoteRow.getAccountId()
                    + ", Note Id:  " + accountNoteRow.getNoteId()
                    + " | Error: "
                    + ExceptionUtils.getRootCause(ex).getMessage();

                log.error(error);
                failed.add(error);
            }
        }
        failed.add("Statistics: Total: " + accountNoteRows.size() + ". Failed: " + failed.size());

        return failed;
    }

    private List<String> createAccountNote(final AccountNoteRow accountNoteRow,
                                           final List<AccountNoteFileRow> files) {

        final Long accountId = accountNoteRow.getAccountId();
        final Optional<Account> migratedAccount = accountRepository.findById(accountId);

        if (migratedAccount.isEmpty()) {

            final String error = "Account Id: " + accountNoteRow.getAccountId()
                + ", Note Id:  " + accountNoteRow.getNoteId()
                + " | Error: Account does not exist";

            log.warn(error);
            return List.of(error);

        } else {

            final Map<UUID, String> filesMap = files.stream()
                .collect(Collectors.toMap(AccountNoteFileRow::getAttachmentId, AccountNoteFileRow::getFileName));

            final AccountNote accountNote = AccountNote.builder()
                .accountId(accountId)
                .payload(NotePayload.builder().note(accountNoteRow.getPayload()).files(filesMap).build())
                .lastUpdatedOn(accountNoteRow.getDateCreated())
                .submitter(accountNoteRow.getSubmitter())
                .submitterId(MigrationConstants.MIGRATION_PROCESS_USER)
                .build();

            accountNoteRepository.save(accountNote);

            for (final AccountNoteFileRow file : files) {

                final FileNote fileNote = FileNote.builder()
                    .accountId(file.getAccountId())
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

            return List.of();
        }
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
