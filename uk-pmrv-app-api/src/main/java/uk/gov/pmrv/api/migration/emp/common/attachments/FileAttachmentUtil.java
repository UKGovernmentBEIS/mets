package uk.gov.pmrv.api.migration.emp.common.attachments;

import java.io.IOException;
import java.util.UUID;

import org.apache.tika.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.common.utils.MimeTypeUtils;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.migration.MigrationConstants;

@Log4j2
@UtilityClass
public class FileAttachmentUtil {

	public FileAttachment getFileAttachment(String filepath) {
		try {
			byte[] content = new ClassPathResource(filepath).getInputStream().readAllBytes();

	        return FileAttachment.builder()
	        	.uuid(UUID.randomUUID().toString())
	        	.fileName(FilenameUtils.getName(filepath))
	            .fileContent(content)
	            .fileSize(content.length)
	            .fileType(MimeTypeUtils.detect(content, filepath))
	            .status(FileStatus.SUBMITTED)
	            .createdBy(MigrationConstants.MIGRATION_PROCESS_USER)
	            .build();
	    } catch (IOException e) {
			log.error(String.format("Error occurred while reading file: %s",  filepath), e);
			return null;
		}
	}   
}
