package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PermitIssuanceRequestQueryService {

    private final RequestRepository requestRepository;

    public PermitIssuanceRequestMetadata findPermitIssuanceMetadataByAccountId(Long accountId) {
        List<Request> requests = requestRepository.findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED);

        if (requests.size() != 1) {
            log.error(String.format(
                    "For account id: %d, DB doesnt contain exactly one approved permit issuance request. Requests number found: %d",
                    accountId, requests.size()));
            //PERMIT_ISSUANCE flow is not migrated for some accounts either because it does not exist or because it was performed prior to 1/1/2021
            return PermitIssuanceRequestMetadata.builder()
                    .type(RequestMetadataType.PERMIT_ISSUANCE)
                    .build();
        }

        //For migrated flows metadata are not migrated
        return Optional.ofNullable((PermitIssuanceRequestMetadata) requests.iterator().next().getMetadata())
                .orElse(PermitIssuanceRequestMetadata.builder()
                        .type(RequestMetadataType.PERMIT_ISSUANCE)
                        .build());
    }
}
