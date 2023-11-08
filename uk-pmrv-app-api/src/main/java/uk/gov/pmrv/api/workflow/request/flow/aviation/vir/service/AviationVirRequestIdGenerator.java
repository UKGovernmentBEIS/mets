package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class AviationVirRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(final RequestParams params) {

        final Long accountId = params.getAccountId();
        final AviationVirRequestMetadata metaData = (AviationVirRequestMetadata) params.getRequestMetadata();
        final int year = metaData.getYear().getValue();

        return String.format("%s%05d-%d", getPrefix(), accountId, year);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AVIATION_VIR);
    }

    @Override
    public String getPrefix() {
        return "VIR";
    }
}
