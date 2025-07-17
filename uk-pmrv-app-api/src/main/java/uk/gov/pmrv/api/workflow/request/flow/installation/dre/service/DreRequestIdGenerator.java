package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.ReportingRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class DreRequestIdGenerator extends ReportingRequestSequenceRequestIdGenerator {

	public DreRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}

	@Override
	public List<RequestType> getTypes() {
		return List.of(RequestType.DRE);
	}

	@Override
    public String getPrefix() {
        return "DRE";
    }
}