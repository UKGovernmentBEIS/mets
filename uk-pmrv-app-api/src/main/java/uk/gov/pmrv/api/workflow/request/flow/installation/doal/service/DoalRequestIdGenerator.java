package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ReportingRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class DoalRequestIdGenerator extends ReportingRequestSequenceRequestIdGenerator {

    public DoalRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.DOAL);
    }

    @Override
    public String getPrefix() {
        return "DOAL";
    }
}
