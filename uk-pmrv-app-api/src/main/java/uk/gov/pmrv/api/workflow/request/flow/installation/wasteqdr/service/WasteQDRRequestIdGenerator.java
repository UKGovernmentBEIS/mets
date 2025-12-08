package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;

import java.util.List;

@Service
public class WasteQDRRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        WasteQDRRequestMetaData metaData = (WasteQDRRequestMetaData) params.getRequestMetadata();

        return String.format("%s%05d-%d-%s", getPrefix(), accountId, metaData.getYear().getValue(), metaData.getQuarter().name());
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.WASTE_QDR);
    }

    @Override
    public String getPrefix() {
        return "WQDR";
    }
}
