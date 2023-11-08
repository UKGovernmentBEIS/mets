package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import java.time.LocalDateTime;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.AccountTypeBatchReissueRequestTypeMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.BatchReissueMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class BatchReissueSubmittedService {

	private final RequestService requestService;
	private final RequestActionUserInfoResolver requestActionUserInfoResolver;
	
	private static final AccountTypeBatchReissueRequestTypeMapper ACCOUNT_TYPE_BATCH_REISSUE_MAPPER = Mappers
			.getMapper(AccountTypeBatchReissueRequestTypeMapper.class);
	private static final BatchReissueMapper BATCH_REISSUE_MAPPER = Mappers
			.getMapper(BatchReissueMapper.class);
	
	@Transactional
	public void batchReissueSubmitted(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final BatchReissueRequestPayload requestPayload = (BatchReissueRequestPayload) request.getPayload();
		final BatchReissueRequestMetadata batchRequestMetadata = (BatchReissueRequestMetadata) request.getMetadata();
		final String signatoryName = requestActionUserInfoResolver.getUserFullName(requestPayload.getSignatory());
		
		final BatchReissueSubmittedRequestActionPayload actionPayload = BATCH_REISSUE_MAPPER
				.toSubmittedActionPayload(requestPayload, batchRequestMetadata, signatoryName,
						ACCOUNT_TYPE_BATCH_REISSUE_MAPPER.accountTypeToBatchReissueSubmittedRequestActionPayloadType(
								request.getType().getAccountType()));

		LocalDateTime now = LocalDateTime.now();
		request.setSubmissionDate(now);
		batchRequestMetadata.setSubmissionDate(now.toLocalDate());
		
		requestService.addActionToRequest(request, 
				actionPayload, 
				RequestActionType.BATCH_REISSUE_SUBMITTED,
				batchRequestMetadata.getSubmitterId());
	}
}
