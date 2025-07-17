package uk.gov.pmrv.api.migration.permit.digitizedMmp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.migration.permit.digitizedMmp.MmpFileType;
import java.util.List;
import java.util.Optional;


@Repository
public interface MmpFilesMigrationRepository extends JpaRepository<MmpFilesMigrationEntity, Long> {

    @Transactional(readOnly = true)
    List<MmpFilesMigrationEntity> findByAccountId(Long accountId);

    @Transactional(readOnly = true)
    Optional<MmpFilesMigrationEntity> findByAccountIdAndMmpFileType(Long accountId, MmpFileType mmpFileType);

}
