package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

public interface PreviewDocumentHandler {

    @Transactional(readOnly = true)
    FileDTO previewDocument(Long taskId, final DecisionNotification decisionNotification);

    List<DocumentTemplateType> getTypes();
}
