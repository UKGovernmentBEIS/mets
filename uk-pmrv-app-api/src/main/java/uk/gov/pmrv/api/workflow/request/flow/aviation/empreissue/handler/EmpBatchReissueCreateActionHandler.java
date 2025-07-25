package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain.EmpReissueAccountDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.mapper.EmpReissueMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpBatchReissueQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCACreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;

import java.util.HashSet;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmpBatchReissueCreateActionHandler implements RequestCACreateActionHandler<BatchReissueRequestCreateActionPayload> {

	private final EmpBatchReissueQueryService empBatchReissueQueryService;
	private final StartProcessRequestService startProcessRequestService;
	private static final EmpReissueMapper EMP_REISSUE_MAPPER = Mappers
			.getMapper(EmpReissueMapper.class);
	
	@Override
	public String process(CompetentAuthorityEnum ca, BatchReissueRequestCreateActionPayload payload,
						  AppUser appUser) {
		final EmpBatchReissueFilters filters = (EmpBatchReissueFilters) payload.getFilters();
		final Map<Long, EmpReissueAccountDetails> accountsDetails = empBatchReissueQueryService
				.findAccountsByCAAndFilters(appUser.getCompetentAuthority(), filters);
		
		final RequestParams requestParams = RequestParams.builder()
	            .type(RequestType.EMP_BATCH_REISSUE)
	            .competentAuthority(appUser.getCompetentAuthority())
	            .requestPayload(BatchReissueRequestPayload.builder()
	            		.payloadType(RequestPayloadType.EMP_BATCH_REISSUE_REQUEST_PAYLOAD)
	            		.filters(filters)
	            		.signatory(payload.getSignatory())
	            		.build())
	            .requestMetadata(EmpBatchReissueRequestMetadata.builder()
	            		.accountsReports(EMP_REISSUE_MAPPER.toEmpReissueAccountsReports(accountsDetails))
	            		.submitterId(appUser.getUserId())
	            		.submitter(appUser.getFullName())
	            		.type(RequestMetadataType.EMP_BATCH_REISSUE)
	            		.build())
				.processVars(Map.of(BpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountsDetails.keySet()),
						BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, 0))
	            .build();
		
		final Request request = startProcessRequestService.startProcess(requestParams);
        return request.getId();
	}

	@Override
	public RequestCreateActionType getRequestCreateActionType() {
		return RequestCreateActionType.EMP_BATCH_REISSUE;
	}

}
