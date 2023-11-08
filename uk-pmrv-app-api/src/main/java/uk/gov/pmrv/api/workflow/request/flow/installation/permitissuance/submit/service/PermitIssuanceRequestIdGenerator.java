package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountIdBasedRequestIdGenerator;

import java.util.List;

@Service
public class PermitIssuanceRequestIdGenerator extends AccountIdBasedRequestIdGenerator {

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.PERMIT_ISSUANCE);
    }

    @Override
    public String getPrefix() {
        return "AEM";
    }
}
