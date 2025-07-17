package uk.gov.pmrv.api.workflow.request.core.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadCascadable;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestDetailsRepository;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Validated
@Service
@RequiredArgsConstructor
public class RequestQueryService {

    private final RequestRepository requestRepository;
    private final RequestDetailsRepository requestDetailsRepository;
    
    @Transactional(readOnly = true)
    public List<Request> findInProgressRequestsByAccount(Long accountId){
        return requestRepository.findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS);
    }

    public boolean existsRequestById(String requestId){
        return requestRepository.existsById(requestId);
    }
    
    public boolean existByRequestTypeAndRequestStatusAndCompetentAuthority(RequestType type, RequestStatus status, CompetentAuthorityEnum competentAuthority) {
        return requestRepository.existsByTypeAndStatusAndCompetentAuthority(type, status, competentAuthority);
    }
    
    public boolean existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType type, RequestStatus status, Long accountId, Year year) {
        return requestRepository.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(type.name(), status.name(), accountId, year.getValue());
    }

    public RequestDetailsSearchResults findRequestDetailsBySearchCriteria(@Valid RequestSearchCriteria criteria) {
        return requestDetailsRepository.findRequestDetailsBySearchCriteria(criteria);
    }

    public RequestDetailsDTO findRequestDetailsById(String requestId) {
    	return requestDetailsRepository.findRequestDetailsById(requestId)
    				.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND, requestId));
    }
    
    public List<Request> getRelatedRequests(final List<Request> requests) {

        final Set<String> relatedRequestIds = requests.stream()
            .filter(request -> RequestType.getCascadableRequestTypes().contains(request.getType()))
            .map(Request::getPayload)
            .map(RequestPayloadCascadable.class::cast)
            .map(RequestPayloadCascadable::getRelatedRequestId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        return requestRepository.findByIdInAndStatus(relatedRequestIds, RequestStatus.IN_PROGRESS);
    }
}
