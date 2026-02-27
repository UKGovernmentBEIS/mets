package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesReopenAvailabilityService {

    private final RequestService requestService;

    public Boolean isWithholdReopenAvailable(Long accountId) {
        List<Request> requests =
                requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES);

        if (requests.isEmpty()) {
            return false;
        }

        Optional<Request> requestOptional =
                requests.stream().filter(request ->
                        RequestStatus.IN_PROGRESS.equals(request.getStatus())).findFirst();

        return requestOptional.isEmpty();
    }
}
