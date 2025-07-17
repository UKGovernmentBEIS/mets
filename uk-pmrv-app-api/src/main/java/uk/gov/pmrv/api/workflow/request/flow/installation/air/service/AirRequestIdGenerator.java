package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ReportingRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class AirRequestIdGenerator extends ReportingRequestSequenceRequestIdGenerator {

    public AirRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AIR);
    }

    @Override
    public String getPrefix() {
        return "AIR";
    }
}