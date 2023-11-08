package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class ReissueRequestIdGenerator implements RequestIdGenerator {
	
	private static final String REQUEST_ID_FORMATTER = "%s%05d-%s";

    @Override
    public String generate(RequestParams params) {
        final Long accountId = params.getAccountId();
        final ReissueRequestMetadata metaData = (ReissueRequestMetadata) params.getRequestMetadata();
		return String.format(REQUEST_ID_FORMATTER, getPrefix(), accountId, metaData.getBatchRequestId());
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_REISSUE, RequestType.EMP_REISSUE);
    }

    @Override
    public String getPrefix() {
        return "B";
    }

}
