package uk.gov.pmrv.api.migration.permit.digitizedMmp.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface MmpFileAttachmentRepository extends FileAttachmentRepository {

    @Transactional(readOnly = true)
    @Query("SELECT f.uuid FROM FileAttachment f WHERE f.fileName = :fileName")
    Optional<String> findUuidByFileName(@Param("fileName") String fileName);

}
