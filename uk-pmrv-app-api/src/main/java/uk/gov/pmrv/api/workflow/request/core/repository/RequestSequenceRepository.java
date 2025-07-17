package uk.gov.pmrv.api.workflow.request.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

public interface RequestSequenceRepository extends JpaRepository<RequestSequence, Long> {

    Optional<RequestSequence> findByType(RequestType type);
    
    Optional<RequestSequence> findByBusinessIdentifierAndType(String businessIdentifier, RequestType type);
}
