package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class SystemMessageNotificationRequestSequenceRequestIdGenerator extends RequestSequenceRequestIdGenerator {

	public SystemMessageNotificationRequestSequenceRequestIdGenerator(RequestSequenceRepository repository) {
		super(repository);
	}
	
	protected RequestSequence resolveRequestSequence(RequestParams params) {
        RequestType type = params.getType();
		return repository.findByType(type).orElse(new RequestSequence(type));
	}
    
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
    	return String.valueOf(sequenceNo);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.SYSTEM_MESSAGE_NOTIFICATION);
    }

    @Override
    public String getPrefix() {
        return null; //not applicable
    }
}
