package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.common;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class RequestUpdateStatusHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
    
    @Setter
    private FixedValue requestStatus;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        Objects.requireNonNull(requestStatus, "requestStatus field was not injected by BPMN");

        RequestStatus status = RequestStatus.valueOf((String)requestStatus.getValue(execution));
        requestService.updateRequestStatus(requestId, status);
    }
}
