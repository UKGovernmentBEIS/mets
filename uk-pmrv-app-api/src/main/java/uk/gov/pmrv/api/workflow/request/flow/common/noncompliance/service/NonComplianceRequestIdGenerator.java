package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class NonComplianceRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

    public NonComplianceRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.NON_COMPLIANCE, RequestType.AVIATION_NON_COMPLIANCE);
    }

    @Override
    public String getPrefix() {
        return "NC";
    }
}
