package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.message;

import java.util.Map;

import java.util.Objects;
import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.common.engine.impl.el.JuelExpression;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.workflow.bpmn.flowable.handler.utils.ExpressionUtils;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "flowable.process.enabled", havingValue = "true", matchIfMissing = false)
public class MsgOtherProcessHandlerFlowable implements JavaDelegate {
	
	private final RuntimeService runtimeService;
	
	@Setter
	private FixedValue messageName;
	
	@Setter
	private JuelExpression processToMessageBusinessKey;
	
	@Setter
	private FixedValue variables;

	@Override
	public void execute(DelegateExecution execution) {
		Objects.requireNonNull(messageName, "messageName field was not injected by BPMN");
		Objects.requireNonNull(processToMessageBusinessKey, "processToMessageBusinessKey field was not injected by BPMN");
		Objects.requireNonNull(variables, "variables field was not injected by BPMN");

		final String messageNameStr = (String) messageName.getValue(execution);
		final String processToMessageBusinessKeyStr = (String) processToMessageBusinessKey.getValue(execution);
		final Map<String, Object> variablesMap = ExpressionUtils
				.resolveMapFromJsonExpression((String) variables.getValue(execution), execution);
		
		runtimeService.createExecutionQuery().messageEventSubscriptionName(messageNameStr)
				.processInstanceBusinessKey(processToMessageBusinessKeyStr, true)
				.list()
				.forEach(exec -> runtimeService.messageEventReceived(messageNameStr, exec.getId(), variablesMap));
	}

}
