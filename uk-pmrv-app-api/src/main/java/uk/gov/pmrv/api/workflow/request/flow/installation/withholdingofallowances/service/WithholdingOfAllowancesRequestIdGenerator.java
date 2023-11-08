package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountRequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class WithholdingOfAllowancesRequestIdGenerator extends AccountRequestSequenceRequestIdGenerator {

	public WithholdingOfAllowancesRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}
	
	@Override
	public List<RequestType> getTypes() {
		return List.of(RequestType.WITHHOLDING_OF_ALLOWANCES);
	}

	@Override
	public String getPrefix() {
		return "WA";
	}

}
