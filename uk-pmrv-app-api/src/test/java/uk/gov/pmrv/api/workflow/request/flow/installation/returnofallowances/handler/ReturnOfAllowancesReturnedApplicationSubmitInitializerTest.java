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
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesReturnedApplicationSubmitInitializerTest {

    @Mock
    private Request request;

    @InjectMocks
    private ReturnOfAllowancesReturnedApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {
        RequestTaskPayload result = initializer.initializePayload(request);

        ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload expectedPayload =
            ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT_PAYLOAD)
                .build();
        assertEquals(expectedPayload, result);
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> requestTaskTypes = initializer.getRequestTaskTypes();

        assertEquals(Set.of(RequestTaskType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT), requestTaskTypes);
    }
}
