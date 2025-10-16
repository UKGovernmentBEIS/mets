package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;

import java.util.List;

@Service
public class ALRRequestIdGenerator implements RequestIdGenerator {

    static private final String FINAL_LABEL = "FINAL";

    @Override
    public String generate(RequestParams params) {
        Long accountId = params.getAccountId();
        ALRRequestMetaData metaData = (ALRRequestMetaData) params.getRequestMetadata();

        return String.format("%s%05d-%s", getPrefix(), accountId, Boolean.TRUE.equals(metaData.getIsFinal()) ? FINAL_LABEL : String.valueOf(metaData.getYear()));
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.ALR);
    }

    @Override
    public String getPrefix() {
        return "ALR";
    }
}
