package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesWithdrawalApplicationSubmitInitializerTest {

    @Mock
    private Request request;

    @InjectMocks
    private WithholdingOfAllowancesWithdrawalApplicationSubmitInitializer initializer;

    @Test
    void initializePayload() {
        RequestTaskPayload result = initializer.initializePayload(request);

        Assertions.assertEquals(RequestTaskPayloadType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT_PAYLOAD,
            result.getPayloadType());
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> taskTypes = initializer.getRequestTaskTypes();
        Assertions.assertEquals(Set.of(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT), taskTypes);
    }
}

