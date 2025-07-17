package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AerRequestQueryService {

    private final RequestRepository requestRepository;
    private final DateService dateService;


    public List<String> getApprovedPermitNotificationIdsByAccount(Long accountId){
        List<Request> requests = requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.PERMIT_NOTIFICATION,
            RequestStatus.APPROVED, Sort.by(Sort.Direction.DESC, "endDate"));

        return requests.stream()
            .map(Request::getId)
            .collect(Collectors.toList());
    }

    public Optional<LocalDateTime> findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(Long accountId) {

        // In case a permit transfer has occurred and the liable for the aer is the receiver,
        // only a request of type PERMIT_TRANSFER_B exists instead of PERMIT_ISSUANCE.
        // The PERMIT_TRANSFER_B has not been closed yet so the current time is used.
        //PERMIT_ISSUANCE flow is not migrated for some accounts either because it does not exist or because it was performed prior to 1/1/2021. See also PermitIssuanceRequestQueryService
        return requestRepository.findByAccountIdAndTypeInAndStatus(
                accountId,
                Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B),
                RequestStatus.APPROVED
            )
            .stream()
            .findFirst()
            .map(req -> req.getType() == RequestType.PERMIT_ISSUANCE ? req.getEndDate() : dateService.getLocalDateTime());
    }

    public Optional<Request> findAerByAccountIdAndYear(Long accountId, int year) {
        return requestRepository.findAllByAccountIdAndTypeInAndMetadataYear(accountId, List.of(RequestType.AER.name()), year).stream().findFirst();
    }

}
