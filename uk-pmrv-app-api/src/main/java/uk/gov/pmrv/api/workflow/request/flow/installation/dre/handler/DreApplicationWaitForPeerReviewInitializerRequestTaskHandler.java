package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.mapper.DreMapper;

@Service
@RequiredArgsConstructor
public class DreApplicationWaitForPeerReviewInitializerRequestTaskHandler implements InitializeRequestTaskHandler {

	private static final DreMapper DRE_MAPPER = Mappers.getMapper(DreMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();
		final DreApplicationSubmitRequestTaskPayload taskPayload = DRE_MAPPER.toDreApplicationSubmitRequestTaskPayload(
				requestPayload, RequestTaskPayloadType.DRE_WAIT_FOR_PEER_REVIEW_PAYLOAD);
		return taskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.DRE_WAIT_FOR_PEER_REVIEW);
	}

}
