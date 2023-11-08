package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class ReturnOfAllowancesRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public ReturnOfAllowancesRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.RETURN_OF_ALLOWANCES);
    }

    @Override
    public String getPrefix() {
        return "RA";
    }

}
