package uk.gov.pmrv.api.workflow.request.core.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {

    @Override
    Optional<Request> findById(String id);

    @Query(name = Request.NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_STATUS_AND_TYPE_NOT_NOTIFICATION)
    List<Request> findByAccountIdAndStatusAndTypeNotNotification(Long accountId, RequestStatus status);

    @Transactional(readOnly = true)
    List<Request> findByAccountIdAndTypeAndStatus(Long accountId, RequestType type, RequestStatus status, Sort sort);

    @Transactional(readOnly = true)
    List<Request> findByAccountIdAndTypeAndStatus(Long accountId, RequestType type, RequestStatus status);

    @Transactional(readOnly = true)
    List<Request> findByAccountIdAndTypeInAndStatus(Long accountId, Set<RequestType> types, RequestStatus status);

    @Transactional(readOnly = true)
    List<Request> findByAccountIdAndTypeAndStatusOrderByEndDateDesc(Long accountId, RequestType type, RequestStatus status);

    @Transactional(readOnly = true)
    List<Request> findByAccountIdAndTypeInAndStatus(Long accountId, List<RequestType> types, RequestStatus status);

    @Transactional(readOnly = true)
    List<Request> findByIdInAndStatus(Set<String> requestIds, RequestStatus status);
    
    @Transactional(readOnly = true)
    List<Request> findAllByAccountIdAndTypeIsNot(Long accountId, RequestType type);

    @Transactional(readOnly = true)
    List<Request> findAllByAccountIdInAndTypeIsNot(Set<Long> accountIds, RequestType type);
    
    @Transactional(readOnly = true)
    boolean existsByTypeAndStatusAndCompetentAuthority(RequestType type, RequestStatus status, CompetentAuthorityEnum competentAuthority);
    
    @Transactional(readOnly = true)
    @Query(name = Request.NAMED_NATIVE_QUERY_EXIST_BY_TYPE_AND_STATUS_AND_ACCOUNT_ID_AND_METADATA_YEAR, nativeQuery = true)
    boolean existByRequestTypeAndStatusAndAccountIdAndMetadataYear(String type, String status, Long accountId, int year);
    
    @Transactional(readOnly = true)
    @Query(name = Request.NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_TYPE_NOT_IN)
    List<RequestInfoDTO> findAllByAccountIdAndTypeNotIn(Long accountId, List<RequestType> requestTypes);

    @Transactional(readOnly = true)
    @Query(name = Request.NAMED_NATIVE_QUERY_FIND_BY_ACCOUNT_ID_AND_TYPE_IN_AND_METADATA_YEAR, nativeQuery = true)
    List<Request> findAllByAccountIdAndTypeInAndMetadataYear(Long accountId, List<String> types, int year);
}
