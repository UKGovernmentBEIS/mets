package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;

import java.util.List;

@Service
public class HSETIRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        HSETIRequestMetadata metaData = (HSETIRequestMetadata) params.getRequestMetadata();

        return String.format("%s%05d-%d_%d", getPrefix(), accountId, metaData.getAllocationPeriod().getPeriodFrom().getValue(), metaData.getAllocationPeriod().getPeriodTo().getValue());
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.HSE_TI);
    }

    @Override
    public String getPrefix() {
        return "HSETI";
    }
}
