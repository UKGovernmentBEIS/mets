package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.AccountTypeBatchReissueRequestTypeMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.ReissueMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class ReissueAddCompletedRequestActionService {

	private final RequestService requestService;
	private final RequestActionUserInfoResolver requestActionUserInfoResolver;
	private static final ReissueMapper REISSUE_MAPPER = Mappers
			.getMapper(ReissueMapper.class);
	private static final AccountTypeBatchReissueRequestTypeMapper accountTypeBatchReissueRequestTypeMapper = Mappers
			.getMapper(AccountTypeBatchReissueRequestTypeMapper.class);
	
	@Transactional
	public void add(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final ReissueRequestPayload requestPayload = (ReissueRequestPayload) request.getPayload();
		final ReissueRequestMetadata requestMetadata = (ReissueRequestMetadata) request.getMetadata();
		final String signatoryName = requestActionUserInfoResolver.getUserFullName(requestMetadata.getSignatory());

		final ReissueCompletedRequestActionPayload actionPayload = REISSUE_MAPPER.toCompletedActionPayload(
				requestPayload, requestMetadata, signatoryName, accountTypeBatchReissueRequestTypeMapper
						.accountTypeReissueCompletedRequestActionPayloadType(request.getType().getAccountType()));

		requestService.addActionToRequest(request, 
				actionPayload, 
				RequestActionType.REISSUE_COMPLETED,
				requestMetadata.getSubmitterId());
	}
}
