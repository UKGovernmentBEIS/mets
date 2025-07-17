package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;

import java.util.List;

@Service
public class BDRRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        BDRRequestMetadata metaData = (BDRRequestMetadata) params.getRequestMetadata();
        int year = metaData.getYear().getValue();

        return String.format("%s%05d-%d", getPrefix(), accountId, year);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.BDR);
    }

    @Override
    public String getPrefix() {
        return "BDR";
    }
}
