package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesApplicationSubmitInitializerTest {

    @Mock
    private Request request;

    @InjectMocks
    private ReturnOfAllowancesApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {
        ReturnOfAllowances returnOfAllowances = ReturnOfAllowances.builder().build();
        ReturnOfAllowancesRequestPayload requestPayload = ReturnOfAllowancesRequestPayload.builder()
            .returnOfAllowances(returnOfAllowances)
            .build();
        when(request.getPayload()).thenReturn(requestPayload);

        RequestTaskPayload result = initializer.initializePayload(request);

        ReturnOfAllowancesApplicationSubmitRequestTaskPayload expectedPayload =
            ReturnOfAllowancesApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT_PAYLOAD)
                .returnOfAllowances(requestPayload.getReturnOfAllowances())
                .sectionsCompleted(requestPayload.getReturnOfAllowancesSectionsCompleted())
                .build();
        assertEquals(expectedPayload, result);
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();

        assertEquals(Set.of(RequestTaskType.RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT), requestTaskTypes);
    }
}
