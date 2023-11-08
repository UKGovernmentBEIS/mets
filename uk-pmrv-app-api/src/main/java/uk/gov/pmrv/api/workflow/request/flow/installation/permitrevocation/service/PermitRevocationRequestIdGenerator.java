package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class PermitRevocationRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public PermitRevocationRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_REVOCATION);
    }

    @Override
    public String getPrefix() {
        return "AEMR";
    }
}
