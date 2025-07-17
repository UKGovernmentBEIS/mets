package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class PermitVariationRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public PermitVariationRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_VARIATION);
    }

    @Override
    public String getPrefix() {
        return "AEMV";
    }
}
