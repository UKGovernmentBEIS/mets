package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAerRequestQueryService {

    private final RequestRepository requestRepository;

    public Optional<LocalDateTime> findEndDateOfApprovedEmpIssuanceByAccountId(Long accountId) {

        return requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.EMP_ISSUANCE_UKETS, RequestStatus.APPROVED)
                .stream()
                .map(Request::getEndDate)
                .findFirst();
    }
}
