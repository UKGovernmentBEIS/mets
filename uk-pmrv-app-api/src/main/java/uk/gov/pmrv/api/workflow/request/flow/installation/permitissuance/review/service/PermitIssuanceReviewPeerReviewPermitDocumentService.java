package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewDocumentService;

@Service
public class PermitIssuanceReviewPeerReviewPermitDocumentService  extends PermitIssuancePreviewPermitDocumentService  implements PermitPreviewDocumentService {
    public PermitIssuanceReviewPeerReviewPermitDocumentService(RequestTaskService requestTaskService, PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService) {
        super(requestTaskService, permitPreviewCreatePermitDocumentService);
    }

    @Override
    public RequestTaskType getType() {
        return RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;
    }
}
