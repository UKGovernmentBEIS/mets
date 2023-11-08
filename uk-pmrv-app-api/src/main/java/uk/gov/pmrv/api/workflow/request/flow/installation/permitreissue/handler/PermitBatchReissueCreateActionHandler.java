package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.handler;

import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.mapper.PermitReissueMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.PermitBatchReissueQueryService;

@Component
@RequiredArgsConstructor
public class PermitBatchReissueCreateActionHandler implements RequestCreateActionHandler<BatchReissueRequestCreateActionPayload> {

	private final PermitBatchReissueQueryService permitBatchReissueQueryService;
	private final StartProcessRequestService startProcessRequestService;
	private static final PermitReissueMapper PERMIT_REISSUE_MAPPER = Mappers
			.getMapper(PermitReissueMapper.class);
	
	@Override
	public String process(Long accountId, RequestCreateActionType type,
			BatchReissueRequestCreateActionPayload payload, PmrvUser pmrvUser) {
		final PermitBatchReissueFilters filters = (PermitBatchReissueFilters) payload.getFilters();
		final Map<Long, PermitReissueAccountDetails> accountsDetails = permitBatchReissueQueryService
				.findAccountsByCAAndFilters(pmrvUser.getCompetentAuthority(), filters);
		
		final RequestParams requestParams = RequestParams.builder()
	            .type(RequestType.PERMIT_BATCH_REISSUE)
	            .competentAuthority(pmrvUser.getCompetentAuthority())
	            .requestPayload(BatchReissueRequestPayload.builder()
	            		.payloadType(RequestPayloadType.PERMIT_BATCH_REISSUE_REQUEST_PAYLOAD)
	            		.filters(filters)
	            		.signatory(payload.getSignatory())
	            		.build())
	            .requestMetadata(PermitBatchReissueRequestMetadata.builder()
	            		.accountsReports(PERMIT_REISSUE_MAPPER.toPermitReissueAccountsReports(accountsDetails))
	            		.submitterId(pmrvUser.getUserId())
	            		.submitter(pmrvUser.getFullName())
	            		.type(RequestMetadataType.PERMIT_BATCH_REISSUE)
	            		.build())
				.processVars(Map.of(BpmnProcessConstants.ACCOUNT_IDS,
						accountsDetails.keySet().stream()
								.collect(Collectors.toSet()),
						BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, 0))
	            .build();
		final Request request = startProcessRequestService.startProcess(requestParams);
        return request.getId();
	}

	@Override
	public RequestCreateActionType getType() {
		return RequestCreateActionType.PERMIT_BATCH_REISSUE;
	}

}
