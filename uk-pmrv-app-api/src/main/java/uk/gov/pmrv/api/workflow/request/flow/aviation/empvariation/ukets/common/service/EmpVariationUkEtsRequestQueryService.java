package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.service;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.mapper.EmpVariationUkEtsMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsRequestQueryService {

    private final RequestRepository requestRepository;

    private final EmpVariationUkEtsMapper empVariationUkEtsMapper = Mappers.getMapper(EmpVariationUkEtsMapper.class);

    public List<EmpVariationRequestInfo> findApprovedVariationRequests(Long accountId) {

        List<Request> requests = requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId,
                RequestType.EMP_VARIATION_UKETS, RequestStatus.APPROVED);
        return requests.stream()
                .map(empVariationUkEtsMapper::toEmpVariationRequestInfo)
                .toList();
    }
}
