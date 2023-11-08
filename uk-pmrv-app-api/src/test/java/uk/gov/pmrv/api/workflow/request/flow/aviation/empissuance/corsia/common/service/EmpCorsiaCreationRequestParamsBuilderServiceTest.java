package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

class EmpCorsiaCreationRequestParamsBuilderServiceTest {

    private final EmpCorsiaCreationRequestParamsBuilderService requestParamsBuilderService = new EmpCorsiaCreationRequestParamsBuilderService();

    @Test
    void buildRequestParams() {
        Long accountId = 1L;

        RequestParams requestParams = requestParamsBuilderService.buildRequestParams(accountId);

        assertEquals(accountId, requestParams.getAccountId());
        assertEquals(RequestType.EMP_ISSUANCE_CORSIA, requestParams.getType());
        assertEquals(RequestPayloadType.EMP_ISSUANCE_CORSIA_REQUEST_PAYLOAD, requestParams.getRequestPayload().getPayloadType());
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.CORSIA, requestParamsBuilderService.getEmissionTradingScheme());
    }
}