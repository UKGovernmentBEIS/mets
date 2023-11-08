package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service.EmpCreationRequestParamsBuilderService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
public class EmpCorsiaCreationRequestParamsBuilderService implements EmpCreationRequestParamsBuilderService {

    @Override
    public RequestParams buildRequestParams(Long accountId) {;
        return RequestParams.builder()
            .type(RequestType.EMP_ISSUANCE_CORSIA)
            .accountId(accountId)
            .requestPayload(EmpIssuanceCorsiaRequestPayload.builder()
                .payloadType(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD)
                .build()
            )
            .build();
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }
}
