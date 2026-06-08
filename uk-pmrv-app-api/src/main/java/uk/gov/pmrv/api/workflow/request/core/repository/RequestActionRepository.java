package uk.gov.pmrv.api.workflow.request.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestActionRepository extends JpaRepository<RequestAction, Long> {

    List<RequestAction> findAllByRequestId(String requestId);

    Optional<RequestAction> findFirstByRequestIdAndType(String requestId, RequestActionType type);
}
