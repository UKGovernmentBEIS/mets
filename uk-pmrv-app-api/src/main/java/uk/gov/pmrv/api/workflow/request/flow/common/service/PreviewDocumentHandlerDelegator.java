package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PreviewDocumentRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreviewDocumentHandlerDelegator {

    private final List<PreviewDocumentHandler> handlers;
    private final OriginalReviewerResolutionService originalReviewerResolutionService;

    public FileDTO getDocument(final Long taskId, final PreviewDocumentRequest previewDocumentRequest) {

        final PreviewDocumentHandler documentService =
            handlers.stream().filter(s -> s.getTypes().contains(previewDocumentRequest.getDocumentType()))
                .findFirst()
                .orElseThrow(
                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
                );

        final DecisionNotification decisionNotification = previewDocumentRequest.getDecisionNotification();
        originalReviewerResolutionService.resolveAndOverrideSignatoryForPeerReview(taskId, decisionNotification);
        return documentService.previewDocument(taskId, decisionNotification);
    }
}
