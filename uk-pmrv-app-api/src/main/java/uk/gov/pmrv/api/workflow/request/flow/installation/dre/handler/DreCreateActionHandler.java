package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@Component
@RequiredArgsConstructor
public class DreCreateActionHandler implements RequestAccountCreateActionHandler<ReportRelatedRequestCreateActionPayload> {

	private final RequestQueryService requestQueryService;
	private final StartProcessRequestService startProcessRequestService;
	
	@Override
	public String process(Long accountId, ReportRelatedRequestCreateActionPayload payload,
			AppUser appUser) {
		final RequestDetailsDTO aerRequestDetails = requestQueryService.findRequestDetailsById(payload.getRequestId());
		final AerRequestMetadata aerMetadata = (AerRequestMetadata) aerRequestDetails.getRequestMetadata();
		final RequestParams requestParams = RequestParams.builder()
	            .type(RequestType.DRE)
	            .accountId(accountId)
	            .requestMetadata(DreRequestMetadata.builder()
	            		.type(RequestMetadataType.DRE)
	            		.year(aerMetadata.getYear())
	            		.build())
	            .requestPayload(DreRequestPayload.builder()
	                .payloadType(RequestPayloadType.DRE_REQUEST_PAYLOAD)
	                .regulatorAssignee(appUser.getUserId())
	                .reportingYear(aerMetadata.getYear())
	                .initiatorRequest(aerMetadata.getInitiatorRequest())
	                .build())
	            .build();

	        final Request request = startProcessRequestService.startProcess(requestParams);

	        return request.getId();
	}

	@Override
	public RequestCreateActionType getRequestCreateActionType() {
		return RequestCreateActionType.DRE;
	}

}
