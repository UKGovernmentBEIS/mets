package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class PermitTransferARequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public PermitTransferARequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_TRANSFER_A);
    }

    @Override
    public String getPrefix() {
        return "AEMTA";
    }
}
