package uk.gov.pmrv.api.workflow.bpmn.handler.permitreissue;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.bpmn.handler.common.reissue.ReissueCompletedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCompletedService;

@Service
public class PermitBatchPermitReissueCompletedHandler extends ReissueCompletedHandler {

	public PermitBatchPermitReissueCompletedHandler(ReissueCompletedService service) {
        super(service);
    }
	
}
