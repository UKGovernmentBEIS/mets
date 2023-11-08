package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

@Service
public class AviationAccountClosureRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

	public AviationAccountClosureRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AVIATION_ACCOUNT_CLOSURE);
    }

    @Override
    public String getPrefix() {
        return "EMPC";
    }
}
