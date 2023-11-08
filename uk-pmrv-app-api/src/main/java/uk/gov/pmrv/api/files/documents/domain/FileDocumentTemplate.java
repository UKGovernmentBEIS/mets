package uk.gov.pmrv.api.files.documents.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.FileEntity;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@SequenceGenerator(name = "default_file_id_generator", sequenceName = "file_document_template_seq", allocationSize = 1)
@Table(name = "file_document_template")
public class FileDocumentTemplate extends FileEntity {

}
