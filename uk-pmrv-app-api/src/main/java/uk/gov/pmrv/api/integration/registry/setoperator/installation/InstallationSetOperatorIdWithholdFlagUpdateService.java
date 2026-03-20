package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.WithholdFlagRegistryEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationSetOperatorIdWithholdFlagUpdateService {

    private final RequestService requestService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void notifyRegistryWithWithholdFlag(Long accountId) {
        List<Request> requests =
            requestService.getByAccountIdAndType(accountId, RequestType.WITHHOLDING_OF_ALLOWANCES);

        if(requests.isEmpty()) {
            return;
        }

        if(requests.size()>1) {
            throw new BusinessException(MetsErrorCode.INVALID_NUMBER_OF_WORKFLOWS);
        }

        Request request = requests.getFirst();
        if(RequestStatus.IN_PROGRESS.equals(request.getStatus()) || RequestStatus.COMPLETED.equals(request.getStatus())) {
            eventPublisher.publishEvent(WithholdFlagRegistryEvent.builder()
                    .withholdFlag(true).accountId(accountId).year(getWithholdingOfAllowancesYear(request)).requestId(request.getId()).build());
        }
    }

    private Integer getWithholdingOfAllowancesYear(Request request) {
        WithholdingOfAllowancesRequestPayload payload = (WithholdingOfAllowancesRequestPayload) request.getPayload();
        return payload.getWithholdingOfAllowances().getYear();
    }

}
