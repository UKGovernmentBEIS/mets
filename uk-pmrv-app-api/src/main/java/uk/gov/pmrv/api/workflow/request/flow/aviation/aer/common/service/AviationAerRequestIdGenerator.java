package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import java.time.Year;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;


@Service
public class AviationAerRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        AviationAerRequestMetadata metaData = (AviationAerRequestMetadata) params.getRequestMetadata();

        return generate(metaData.getYear(), accountId);
    }

    private String generate(Year year, Long accountId) {
        return String.format("%s%05d-%d", getPrefix(), accountId, year.getValue());
    }

    public String generatePastAerId(Long accountId, Year year, Integer yearsBefore) {
        return generate(year.minusYears(yearsBefore),accountId);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.AVIATION_AER_UKETS, RequestType.AVIATION_AER_CORSIA);
    }

    @Override
    public String getPrefix() {
        return "AEM";
    }
}
