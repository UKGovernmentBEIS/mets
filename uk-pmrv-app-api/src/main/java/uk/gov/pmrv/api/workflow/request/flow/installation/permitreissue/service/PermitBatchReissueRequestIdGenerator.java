package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CompetentAuthoritySequenceRequestIdGenerator;

import java.util.List;

@Service
public class PermitBatchReissueRequestIdGenerator extends CompetentAuthoritySequenceRequestIdGenerator {
	
	public PermitBatchReissueRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}

	@Override
	public List<RequestType> getTypes() {
		return List.of(RequestType.PERMIT_BATCH_REISSUE);
	}

	@Override
	public String getPrefix() {
		return "BRI";
	}

}
