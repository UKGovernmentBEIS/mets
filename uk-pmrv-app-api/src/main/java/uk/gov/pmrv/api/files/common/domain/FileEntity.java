package uk.gov.pmrv.api.files.common.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_file_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "uuid", unique = true)
    @NotBlank
    private String uuid;

    @Column(name = "file_name")
    @NotBlank
    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="file_content")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] fileContent;

    /**
     * The file size (in bytes).
     */
    @Column(name = "file_size")
    @Positive
    private long fileSize;

    @Column(name = "file_type")
    @NotBlank
    private String fileType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private FileStatus status;

    @Column(name = "created_by")
    @NotBlank
    private String createdBy;

    @Column(name = "last_updated_on")
    @LastModifiedDate
    @NotNull
    private LocalDateTime lastUpdatedOn;
}
