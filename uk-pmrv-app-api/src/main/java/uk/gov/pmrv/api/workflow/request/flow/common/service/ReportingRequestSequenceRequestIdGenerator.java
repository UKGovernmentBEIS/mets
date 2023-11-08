package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.time.Year;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataReportable;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

public abstract class ReportingRequestSequenceRequestIdGenerator extends RequestSequenceRequestIdGenerator {
	
	protected static final String REQUEST_ID_FORMATTER = "%s%05d-%d-%d";

	protected ReportingRequestSequenceRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}
	
	protected RequestSequence resolveRequestSequence(RequestParams params) {
		final Long accountId = params.getAccountId();
		final Year year = ((RequestMetadataReportable)params.getRequestMetadata()).getYear();
        final RequestType type = params.getType();
        
        final String businessIdentifierKey = accountId + "-" + year.getValue();
        
		return repository.findByBusinessIdentifierAndType(businessIdentifierKey, type)
				.orElse(new RequestSequence(businessIdentifierKey, type));
	}

	@Override
	protected String generateRequestId(Long sequenceNo, RequestParams params) {
		
		return String.format(REQUEST_ID_FORMATTER,
			getPrefix(),
			params.getAccountId(),
			((RequestMetadataReportable) params.getRequestMetadata()).getYear().getValue(),
			sequenceNo);
	}
}
