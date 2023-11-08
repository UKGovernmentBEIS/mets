package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsApplicationSubmitInitializerTest {
    @InjectMocks
    private EmpIssuanceUkEtsApplicationSubmitInitializer initializer;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Test
    void initializePayload() {
        Long accountId = 1L;
        String name = "name";
        String crcoCode = "crco code";
        Request request = Request.builder().accountId(accountId).build();
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(AviationAccountInfoDTO.builder()
                .name(name)
                .crcoCode(crcoCode)
                .build());
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
                (EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(RequestTaskPayloadType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT_PAYLOAD, requestTaskPayload.getPayloadType());
        assertEquals(name, requestTaskPayload.getEmissionsMonitoringPlan().getOperatorDetails().getOperatorName());
        assertEquals(crcoCode, requestTaskPayload.getEmissionsMonitoringPlan().getOperatorDetails().getCrcoCode());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT);
    }
}