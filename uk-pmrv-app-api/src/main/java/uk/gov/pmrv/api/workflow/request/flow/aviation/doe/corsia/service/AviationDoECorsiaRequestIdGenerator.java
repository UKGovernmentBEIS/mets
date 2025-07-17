package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ReportingRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class AviationDoECorsiaRequestIdGenerator extends ReportingRequestSequenceRequestIdGenerator {

    public AviationDoECorsiaRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AVIATION_DOE_CORSIA);
    }

    @Override
    public String getPrefix() {
        return "DOECOR";
    }
}
