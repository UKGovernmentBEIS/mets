package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.mapper.EmpVariationCorsiaMapper;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaRequestQueryService {

    private final RequestRepository requestRepository;

    private final EmpVariationCorsiaMapper empVariationCorsiaMapper = Mappers.getMapper(EmpVariationCorsiaMapper.class);

    public List<EmpVariationRequestInfo> findApprovedVariationRequests(Long accountId) {

        List<Request> requests = requestRepository.findByAccountIdAndTypeAndStatusOrderByEndDateDesc(accountId,
                RequestType.EMP_VARIATION_CORSIA, RequestStatus.APPROVED);
        return requests.stream()
                .map(empVariationCorsiaMapper::toEmpVariationRequestInfo)
                .toList();
    }
}
