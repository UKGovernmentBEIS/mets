package uk.gov.pmrv.api.workflow.bpmn.handler.empreissue;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.bpmn.handler.common.reissue.ReissueCompletedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCompletedService;

@Service
public class EmpBatchEmpReissueCompletedHandler extends ReissueCompletedHandler {

	public EmpBatchEmpReissueCompletedHandler(ReissueCompletedService service) {
        super(service);
    }
	
}
