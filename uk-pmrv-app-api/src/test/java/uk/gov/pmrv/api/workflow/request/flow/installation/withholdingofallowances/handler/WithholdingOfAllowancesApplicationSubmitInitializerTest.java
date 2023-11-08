package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesApplicationSubmitInitializerTest {

    @Mock
    private Request request;

    @InjectMocks
    private WithholdingOfAllowancesApplicationSubmitInitializer initializer;

    @Test
    void initializePayload_shouldReturnCorrectPayload() {
        WithholdingOfAllowances withholdingOfAllowances = WithholdingOfAllowances.builder().build();
        WithholdingOfAllowancesRequestPayload requestPayload = WithholdingOfAllowancesRequestPayload.builder()
            .withholdingOfAllowances(withholdingOfAllowances)
            .build();
        when(request.getPayload()).thenReturn(requestPayload);

        RequestTaskPayload result = initializer.initializePayload(request);

        WithholdingOfAllowancesApplicationSubmitRequestTaskPayload expectedPayload =
            WithholdingOfAllowancesApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD)
                .withholdingOfAllowances(requestPayload.getWithholdingOfAllowances())
                .sectionsCompleted(requestPayload.getWithholdingOfAllowancesSectionsCompleted())
                .build();
        assertEquals(expectedPayload, result);
    }

    @Test
    void getRequestTaskTypes_shouldReturnSetOfWithholdingOfAllowancesApplicationSubmitType() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();

        assertEquals(Set.of(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT), requestTaskTypes);
    }
}
