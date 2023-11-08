package uk.gov.pmrv.api.workflow.bpmn.handler.message;

import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class MsgOtherProcessHandler implements JavaDelegate {

    @Override
    public void execute(final DelegateExecution execution) {
        
        final Map<String, Object> variables = execution.hasVariable(BpmnProcessConstants.VARIABLES) ?
            (Map<String, Object>) execution.getVariables().get(BpmnProcessConstants.VARIABLES) : Map.of();

        final ThrowEvent messageEvent = (ThrowEvent) execution.getBpmnModelElementInstance();
        final MessageEventDefinition messageEventDefinition = 
            (MessageEventDefinition) messageEvent.getEventDefinitions().iterator().next();
        final String messageName = messageEventDefinition.getMessage().getName();
        if (!execution.hasVariable(BpmnProcessConstants.PROCESS_TO_MESSAGE_BUSINESS_KEY)) {
            throw new RuntimeException(String.format(
                "No processToMessageBusinessKey variable set for message %s in process instance id %s",
                messageName, execution.getProcessInstanceId())
            );
        }
        final String businessKey = (String) execution.getVariable(BpmnProcessConstants.PROCESS_TO_MESSAGE_BUSINESS_KEY);
        execution.getProcessEngineServices()
            .getRuntimeService()
            .createMessageCorrelation(messageName)
            .processInstanceBusinessKey(businessKey)
            .setVariables(variables)
            .correlateAll();
    }
}
