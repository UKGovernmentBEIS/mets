package uk.gov.pmrv.api.workflow.request.core.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;

public interface RequestNoteRepository extends JpaRepository<RequestNote, Long> {

    @Transactional(readOnly = true)
    Page<RequestNote> findRequestNotesByRequestIdOrderByLastUpdatedOnDesc(Pageable pageable, String requestId);

    @Transactional(readOnly = true)
    @Query(
        "select request " +
        "from RequestNote requestNote join Request request " +
        "on requestNote.requestId = request.id " +
        "where requestNote.id = :id"
    )
    Optional<Request> getRequestByNoteId(Long id);
}
