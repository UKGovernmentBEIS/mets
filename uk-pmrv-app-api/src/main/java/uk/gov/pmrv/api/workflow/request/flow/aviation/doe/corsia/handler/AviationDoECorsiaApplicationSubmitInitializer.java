package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.mapper.AviationDoECorsiaMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaApplicationSubmitInitializer  implements InitializeRequestTaskHandler {


	private static final AviationDoECorsiaMapper DOE_MAPPER = Mappers.getMapper(AviationDoECorsiaMapper.class);


	@Override
	public RequestTaskPayload initializePayload(Request request) {

		final AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
		final AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload;

		if (doeExists(requestPayload)) {
			taskPayload = DOE_MAPPER.toAviationDoECorsiaApplicationSubmitRequestTaskPayload(requestPayload,
				RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD);
		} else {
	        taskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
				.payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD).build();
		}


		return taskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT);
	}

	private boolean doeExists(AviationDoECorsiaRequestPayload requestPayload) {
		return requestPayload.getDoe() != null;
	}


}
