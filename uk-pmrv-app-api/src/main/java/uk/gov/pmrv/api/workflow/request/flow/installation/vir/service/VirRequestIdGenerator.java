package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;

import java.util.List;

@Service
public class VirRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        VirRequestMetadata metaData = (VirRequestMetadata) params.getRequestMetadata();
        int year = metaData.getYear().getValue();

        return String.format("%s%05d-%d", getPrefix(), accountId, year);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.VIR);
    }

    @Override
    public String getPrefix() {
        return "VIR";
    }
}
