package uk.gov.pmrv.api.emissionsmonitoringplan.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpAccountDTO;

import java.util.Optional;
import java.util.Set;

@Repository
public interface EmissionsMonitoringPlanRepository extends JpaRepository<EmissionsMonitoringPlanEntity, String> {

    Optional<EmissionsMonitoringPlanEntity> findByAccountId(Long accountId);

    @Transactional(readOnly = true)
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_QUERY_FIND_EMP_ID_BY_ACCOUNT_ID)
    Optional<String> findEmpIdByAccountId(Long accountId);
    
    @Transactional(readOnly = true)
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_NATIVE_QUERY_FIND_ALL_BY_ACCOUNT_IDS, nativeQuery = true)
    Set<EmpAccountDTO> findAllByAccountIdIn(Set<Long> accountIds);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID_BY_ID)
    void updateFileDocumentUuid(@Param("empId") String empId, @Param("fileDocumentUUid") String fileDocumentUUid);

    @Transactional(readOnly = true)
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_NATIVE_QUERY_FIND_BY_ATTACHMENT_UUID, nativeQuery = true)
    Optional<EmpAccountDTO> findEmpAccountByAttachmentUuid(
        @Param("attachmentUuid") String attachmentUuid);

    @Transactional
    @Modifying
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_QUERY_UPDATE_FILE_DOCUMENT_UUID_BY_ACCOUNT_ID)
    void updateFileDocumentUuidByAccountId(@Param("fileDocumentUUid") String fileDocumentUUid, @Param("accountId") Long accountId);

    @Transactional(readOnly = true)
    Optional<EmissionsMonitoringPlanEntity> findEmpByIdAndFileDocumentUuid(String empId, String fileDocumentUuid);

    @Transactional(readOnly = true)
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_QUERY_FIND_EMP_ACCOUNT_BY_ID)
    Optional<Long> findEmpAccountById(String id);

    @Transactional
    @Modifying
    @Query(name = EmissionsMonitoringPlanEntity.NAMED_QUERY_UPDATE_CONSOLIDATION_NUMBER_BY_ACCOUNT_ID)
    void updateConsolidationNumberByAccountId(@Param("consolidationNumber") int consolidationNumber, @Param("accountId") Long accountId);
}
