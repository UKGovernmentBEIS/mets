package uk.gov.pmrv.api.workflow.request.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;

import java.util.List;

@Repository
public interface RequestActionRepository extends JpaRepository<RequestAction, Long> {

    List<RequestAction> findAllByRequestId(String requestId);
}
