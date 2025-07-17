package uk.gov.pmrv.api.migration.permit.digitizedMmp.repository;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.MmpFileType;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@SequenceGenerator(name = "default_file_id_generator", sequenceName = "mmp_files_migration_seq", allocationSize = 1)
@Table(name = "mmp_files_migration")
public class MmpFilesMigrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_file_id_generator")
    private Long id;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @Column(name = "permit_id")
    private String permitId;

    @Column(name = "file_uuid")
    private String fileUuid;

    @Column(name = "file_name")
    @NotNull
    private String fileName;

    @Column(name = "file_type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private MmpFileType mmpFileType;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name="file_content")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] fileContent;

    private MmpFilesMigrationEntity(Long id, Long accountId, String permitId, String fileUuid, String fileName, MmpFileType mmpFileType, byte[] fileContent) {
        this.id = id;
        this.accountId = accountId;
        this.permitId = permitId;
        this.fileUuid = fileUuid;
        this.fileName = fileName;
        this.mmpFileType = mmpFileType;
        this.fileContent = fileContent;
    }

    @Builder
    public static MmpFilesMigrationEntity createMmpFilesMigrationEntity(Long id, Long accountId, String permitId, String fileUuid, String fileName, MmpFileType mmpFileType, byte[] fileContent) {
        return new MmpFilesMigrationEntity(id, accountId, permitId, fileUuid, fileName, mmpFileType, fileContent);
    }

}
