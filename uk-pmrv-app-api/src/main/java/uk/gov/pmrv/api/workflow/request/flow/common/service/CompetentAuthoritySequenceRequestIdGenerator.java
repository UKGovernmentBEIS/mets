package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

public abstract class CompetentAuthoritySequenceRequestIdGenerator extends RequestSequenceRequestIdGenerator {

	private static final String REQUEST_ID_FORMATTER = "%s%04d-%s";

	protected CompetentAuthoritySequenceRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}

	@Override
	protected RequestSequence resolveRequestSequence(RequestParams params) {
		final CompetentAuthorityEnum competentAuthority = params.getCompetentAuthority();
        final RequestType type = params.getType();
        
		return repository.findByBusinessIdentifierAndType(competentAuthority.name(), type)
				.orElse(new RequestSequence(competentAuthority.name(), type));
	}

	@Override
	protected String generateRequestId(Long sequenceNo, RequestParams params) {
		return String.format(REQUEST_ID_FORMATTER, getPrefix(), sequenceNo,
				params.getCompetentAuthority().getOneLetterCode());
	}
	
}
