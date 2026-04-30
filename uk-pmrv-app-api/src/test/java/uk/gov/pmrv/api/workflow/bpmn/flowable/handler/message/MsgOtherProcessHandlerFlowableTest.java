package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.message;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.DefaultExpressionManager;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.common.engine.impl.el.JuelExpression;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MsgOtherProcessHandlerFlowableTest {

	@InjectMocks
    private MsgOtherProcessHandlerFlowable cut;

    @Mock
    private DelegateExecution execution;

    @Mock
    private RuntimeService runtimeService;
    
    @Test
    void execute() throws Exception {
    	String messageNameStr = "eventMessage1";
    	String processToMessageBusinessKeyStr = "processToMessageBusinessKeyStr";
    	cut.setMessageName(new FixedValue(messageNameStr));
		cut.setVariables(new FixedValue(
				"{\"reissueRequestSucceeded\": \"true\", \"accountId\": \"1\", \"reissueRequestId\": \"reissueRequest123\"}"));
    	
        ExpressionManager expressionManager = new DefaultExpressionManager(Collections.emptyMap());
        Expression expression = expressionManager.createExpression("${'processToMessageBusinessKeyStr'}");
        JuelExpression juelExpr = (JuelExpression) expression;
        cut.setProcessToMessageBusinessKey(juelExpr);
        
        ExecutionQuery executionQueryCreate = Mockito.mock(ExecutionQuery.class);
        when(runtimeService.createExecutionQuery()).thenReturn(executionQueryCreate);
        
        ExecutionQuery executionQueryForEvent = Mockito.mock(ExecutionQuery.class);
        when(executionQueryCreate.messageEventSubscriptionName(messageNameStr)).thenReturn(executionQueryForEvent);
        
        ExecutionQuery executionQueryForBusinessKey = Mockito.mock(ExecutionQuery.class);
		when(executionQueryForEvent.processInstanceBusinessKey(processToMessageBusinessKeyStr, true))
				.thenReturn(executionQueryForBusinessKey);
		
		Execution executionForBusinessKeyListItem1 = Mockito.mock(Execution.class);
		when(executionForBusinessKeyListItem1.getId()).thenReturn("exec1");
		List<Execution> finalExecutionQueryList = List.of(executionForBusinessKeyListItem1);
		when(executionQueryForBusinessKey.list()).thenReturn(finalExecutionQueryList);

        // Invoke
        cut.execute(execution);

        // Verify
        verify(runtimeService, times(1)).messageEventReceived(messageNameStr, "exec1", Map.of(
        		"reissueRequestSucceeded", "true",
        		"accountId", "1",
        		"reissueRequestId", "reissueRequest123"
        		));
        
    }
    
}
