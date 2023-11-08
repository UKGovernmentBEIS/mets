package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountIdBasedRequestIdGenerator;

import java.util.List;

@Service
public class EmpIssuanceRequestIdGenerator extends AccountIdBasedRequestIdGenerator {

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.EMP_ISSUANCE_UKETS, RequestType.EMP_ISSUANCE_CORSIA);
    }

    @Override
    public String getPrefix() {
        return "EMP";
    }
}
