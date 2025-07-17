package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit;

import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

public interface PermitPreviewDocumentService {

    FileDTO create(Long taskId, DecisionNotification decisionNotification);
    
    RequestTaskType getType();
}
