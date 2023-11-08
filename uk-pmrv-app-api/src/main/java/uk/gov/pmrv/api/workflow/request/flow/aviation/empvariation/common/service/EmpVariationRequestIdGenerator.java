package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

@Service
public class EmpVariationRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

	public EmpVariationRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.EMP_VARIATION_UKETS, RequestType.EMP_VARIATION_CORSIA);
    }

    @Override
    public String getPrefix() {
        return "EMPV";
    }
}
