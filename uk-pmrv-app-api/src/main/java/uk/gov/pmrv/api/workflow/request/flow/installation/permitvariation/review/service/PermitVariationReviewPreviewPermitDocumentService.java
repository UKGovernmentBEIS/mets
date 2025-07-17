package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;


import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationPreviewPermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

@Service
public class PermitVariationReviewPreviewPermitDocumentService extends PermitVariationPreviewPermitDocumentService implements PermitPreviewDocumentService  {
    public PermitVariationReviewPreviewPermitDocumentService(RequestTaskService requestTaskService, PermitQueryService permitQueryService, DateService dateService, PermitVariationRequestQueryService permitVariationRequestQueryService, PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService) {
        super(requestTaskService, permitQueryService, dateService, permitVariationRequestQueryService, permitPreviewCreatePermitDocumentService);
    }

    @Override
    public RequestTaskType getType() {
        return RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW;
    }
}
