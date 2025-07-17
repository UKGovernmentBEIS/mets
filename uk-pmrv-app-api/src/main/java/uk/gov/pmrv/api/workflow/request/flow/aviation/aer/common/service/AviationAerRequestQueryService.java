package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;


@Service
@RequiredArgsConstructor
public class AviationAerRequestQueryService {

    private final RequestRepository requestRepository;

    public Optional<Request> findAviationAerById(String requestId) {
        return requestRepository.findById(requestId).stream().findFirst();
    }

    public Optional<LocalDateTime> findEndDateOfApprovedEmpIssuanceByAccountId(Long accountId,
                                                                               RequestType requestType) {

        return requestRepository.findByAccountIdAndTypeAndStatus(accountId, requestType, RequestStatus.APPROVED)
                .stream()
                .map(Request::getEndDate)
                .findFirst();
    }

    public Optional<Request> findRequestByAccountAndTypeForYear(Long accountId,
                                                            RequestType requestType, Year year){
        return requestRepository.findAllByAccountIdAndTypeInAndMetadataYear(
                accountId, List.of(requestType.name()), year.getValue()).stream().findFirst();
    }
}
