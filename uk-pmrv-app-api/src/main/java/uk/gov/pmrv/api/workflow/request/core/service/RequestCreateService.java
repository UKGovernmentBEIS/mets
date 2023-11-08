package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@RequiredArgsConstructor
@Service
public class RequestCreateService {

    private final RequestRepository requestRepository;
    private final AccountQueryService accountQueryService;

    /**
     * Create and persist request.
     *
     * @param requestParams the {@link RequestParams}
     * @param status        the {@link RequestStatus}
     * @return the request created
     */
    @Transactional
    public Request createRequest(RequestParams requestParams, RequestStatus status) {
        Request request = new Request();
        request.setId(requestParams.getRequestId());
        request.setType(requestParams.getType());
        request.setStatus(status);
        request.setCompetentAuthority(resolveRequestCompetentAuthority(requestParams));
        request.setVerificationBodyId(resolveRequestVerificationBody(requestParams));
        request.setAccountId(requestParams.getAccountId());
        request.setPayload(requestParams.getRequestPayload());
        request.setMetadata(requestParams.getRequestMetadata());
        if (requestParams.getCreationDate() != null) {
            request.setCreationDate(requestParams.getCreationDate());
        }

        return requestRepository.save(request);
    }

    private CompetentAuthorityEnum resolveRequestCompetentAuthority(RequestParams requestParams) {
		return requestParams.getCompetentAuthority() != null ? requestParams.getCompetentAuthority()
				: accountQueryService.getAccountCa(requestParams.getAccountId());
    }

    private Long resolveRequestVerificationBody(RequestParams requestParams) {
		return requestParams.getAccountId() != null
				? accountQueryService.getAccountVerificationBodyId(requestParams.getAccountId()).orElse(null)
				: null;
    }
}
