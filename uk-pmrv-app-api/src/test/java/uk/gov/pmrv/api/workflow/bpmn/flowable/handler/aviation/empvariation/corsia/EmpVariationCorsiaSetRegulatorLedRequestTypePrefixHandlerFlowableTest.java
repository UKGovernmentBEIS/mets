package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.empvariation.corsia;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaSetRegulatorLedRequestTypePrefixHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private EmpVariationCorsiaSetRegulatorLedRequestTypePrefixHandlerFlowable handler;

    @Test
    void execute_setsRequestTypeDynamicTaskPrefixVariable() {
        handler.execute(execution);

        verify(execution).setVariable(
            BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX,
            RequestCustomContext.EMP_VARIATION_CORSIA_REGULATOR_LED.getCode()
        );
    }
}
