package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp;

import java.util.List;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

public interface EmpPreviewDocumentService {

    FileDTO create(Long taskId, DecisionNotification decisionNotification);
    
    List<RequestTaskType> getTypes();
}
