package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.mapper.PermitVariationMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationRequestQueryService {

    private final RequestRepository requestRepository;
    private final PermitVariationMapper permitVariationMapper = Mappers.getMapper(PermitVariationMapper.class);

    public List<PermitVariationRequestInfo> findPermitVariationRequests(Long accountId) {
        List<Request> requests = requestRepository.findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_VARIATION, RequestStatus.APPROVED, Sort.by(Sort.Direction.ASC, "creationDate"));
        return requests.stream()
                .map(permitVariationMapper::toPermitVariationRequestInfo)
                .toList();
    }

    public List<PermitVariationRequestInfo> findApprovedPermitVariationRequests(Long accountId) {

        List<Request> requests = requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId,
                RequestType.PERMIT_VARIATION, RequestStatus.APPROVED);
        return requests.stream()
                .map(permitVariationMapper::toPermitVariationRequestInfo)
                .toList();
    }
}
