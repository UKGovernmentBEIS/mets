package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

public abstract class AccountRequestSequenceRequestIdGenerator extends RequestSequenceRequestIdGenerator {
	
	private static final String REQUEST_ID_FORMATTER = "%s%05d-%d";

	public AccountRequestSequenceRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}
	
	protected RequestSequence resolveRequestSequence(RequestParams params) {
		final Long accountId = params.getAccountId();
        final RequestType type = params.getType();
        
		return repository.findByBusinessIdentifierAndType(String.valueOf(accountId), type)
				.orElse(new RequestSequence(String.valueOf(accountId), type));
	}
    
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
    	return String.format(REQUEST_ID_FORMATTER, getPrefix(), params.getAccountId(), sequenceNo);
    }

}
