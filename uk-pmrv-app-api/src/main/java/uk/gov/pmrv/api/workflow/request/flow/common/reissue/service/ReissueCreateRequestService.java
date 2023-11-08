package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.AccountTypeBatchReissueRequestTypeMapper;

@Service
@RequiredArgsConstructor
public class ReissueCreateRequestService {

	private final RequestService requestService;
	private final StartProcessRequestService startProcessRequestService;
	private final AccountTypeBatchReissueRequestTypeMapper accountTypeBatchReissueRequestTypeMapper = Mappers
			.getMapper(AccountTypeBatchReissueRequestTypeMapper.class);
	
	@Transactional
	public void createReissueRequest(Long accountId, String batchRequestId, String batchRequestBusinessKey) {
		final Request batchRequest = requestService.findRequestById(batchRequestId);
		final BatchReissueRequestPayload batchRequestPayload = (BatchReissueRequestPayload) batchRequest.getPayload();
		final BatchReissueRequestMetadata batchRequestMetadata = (BatchReissueRequestMetadata) batchRequest.getMetadata();
		
		final RequestParams requestParams = RequestParams.builder()
				.type(accountTypeBatchReissueRequestTypeMapper
						.accountTypeToReissueRequestType(batchRequest.getType().getAccountType()))
				.accountId(accountId)
				.requestPayload(ReissueRequestPayload.builder()
						.payloadType(RequestPayloadType.REISSUE_REQUEST_PAYLOAD)
						.build())
				.requestMetadata(ReissueRequestMetadata.builder()
						.type(RequestMetadataType.REISSUE)
						.batchRequestId(batchRequestId)
						.signatory(batchRequestPayload.getSignatory())
						.submitterId(batchRequestMetadata.getSubmitterId())
						.submitter(batchRequestMetadata.getSubmitter())
						.build())
				.processVars(Map.of(
						BpmnProcessConstants.BATCH_REQUEST_BUSINESS_KEY, batchRequestBusinessKey,
						BpmnProcessConstants.ACCOUNT_ID, accountId
						))
				.build();
		
		startProcessRequestService.startProcess(requestParams);
	}
}
