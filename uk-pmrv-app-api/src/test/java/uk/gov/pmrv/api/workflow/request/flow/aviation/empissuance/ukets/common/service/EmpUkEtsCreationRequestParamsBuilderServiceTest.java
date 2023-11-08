package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmpUkEtsCreationRequestParamsBuilderServiceTest {

    private final EmpUkEtsCreationRequestParamsBuilderService requestParamsBuilderService = new EmpUkEtsCreationRequestParamsBuilderService();

    @Test
    void buildRequestParams() {
        Long accountId = 1L;

        RequestParams requestParams = requestParamsBuilderService.buildRequestParams(accountId);

        assertEquals(accountId, requestParams.getAccountId());
        assertEquals(RequestType.EMP_ISSUANCE_UKETS, requestParams.getType());
        assertEquals(RequestPayloadType.EMP_ISSUANCE_UKETS_REQUEST_PAYLOAD, requestParams.getRequestPayload().getPayloadType());
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, requestParamsBuilderService.getEmissionTradingScheme());
    }
}