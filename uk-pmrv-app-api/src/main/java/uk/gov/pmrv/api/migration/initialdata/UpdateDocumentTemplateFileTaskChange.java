package uk.gov.pmrv.api.migration.initialdata;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import lombok.Getter;
import lombok.Setter;
import uk.gov.netz.api.common.domain.ResourceFile;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateDocumentTemplateFileTaskChange extends DocumentTemplateFileUploadTaskChange implements CustomTaskChange {

    private DocumentTemplateType type;

    private static final String QUERY =
            "with t as ( \r\n "
            + " select fdt.id as row_id \r\n"
            + " from file_document_template fdt \r\n"
            + " inner join notification_document_template ndt on ndt.file_document_template_id = fdt.id \r\n"
            + " where ndt.competent_authority = ? \r\n"
            + " and ndt.type = ? \r\n"
            + " and ndt.account_type = ? \r\n"
            + ") \r\n"
            + "update file_document_template \r\n"
            + "set file_name = ?, \r\n"
            + " file_content = ?, \r\n"
            + " file_size = ?, \r\n"
            + " file_type = ?, \r\n"
            + " last_updated_on = ? \r\n"
            + "from t \r\n"
            + "where id = t.row_id \r\n";

    @Override
    public void execute(Database database) throws CustomChangeException {
        // The context classloader does not include the jar file that contain the resources files,
        // so the code in ResourceFileUtils class trying to find resource using Thread.currentThread() will not work.
        // Solution: set the current class' classloader as the thread's classloader
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        
        ResourceFile documentTemplateFileResource = findCaTemplateResourceFile();
        
        try {
            JdbcConnection conn = (JdbcConnection) database.getConnection();

            PreparedStatement insertFileDocumentStmnt = conn.prepareStatement(QUERY);
            insertFileDocumentStmnt.setString(1, getCompetentAuthority().name());
            insertFileDocumentStmnt.setString(2, getType().name());
            insertFileDocumentStmnt.setString(3, getAccountType().name());
            insertFileDocumentStmnt.setString(4, getFileDocumentName());
            insertFileDocumentStmnt.setBytes(5, documentTemplateFileResource.getFileContent());
            insertFileDocumentStmnt.setLong(6, documentTemplateFileResource.getFileSize());
            insertFileDocumentStmnt.setString(7, documentTemplateFileResource.getFileType());
            insertFileDocumentStmnt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int updatedCount = insertFileDocumentStmnt.executeUpdate();
            if(updatedCount == 0) {
            	throw new CustomChangeException(String.format("No file document template found for CA: %s and type: %s and account type: %s",
                    getCompetentAuthority().name(), getType().name(), getAccountType().name()));
            }
        } catch (DatabaseException | SQLException e) {
            throw new CustomChangeException(e.getMessage());
        }
    }

}