package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

public abstract class AccountIdBasedRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        return String.format("%s%05d", getPrefix(), accountId);
    }
}
