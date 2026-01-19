package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;

import java.util.List;

@Service
public class BDRS2RequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        BDRS2RequestMetadata metaData = (BDRS2RequestMetadata) params.getRequestMetadata();
        int year = metaData.getYear().getValue();

        return String.format("%s-%05d-%d", getPrefix(), accountId, year);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.BDRS2);
    }

    @Override
    public String getPrefix() {
        return "BDRS2";
    }
}
