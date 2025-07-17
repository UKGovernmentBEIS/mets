package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class PermitSurrenderRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public PermitSurrenderRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_SURRENDER);
    }

    @Override
    public String getPrefix() {
        return "AEMS";
    }
}
