package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class PermitNotificationRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public PermitNotificationRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_NOTIFICATION);
    }

    @Override
    public String getPrefix() {
        return "AEMN";
    }
}
