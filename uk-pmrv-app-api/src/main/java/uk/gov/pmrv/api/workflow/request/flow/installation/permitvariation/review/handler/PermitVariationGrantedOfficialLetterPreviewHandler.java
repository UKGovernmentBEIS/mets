package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

@Service
public class PermitVariationGrantedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final PermitQueryService permitQueryService;
    
    public PermitVariationGrantedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                              final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                              final DocumentFileGeneratorService documentFileGeneratorService,
                                                              final PermitQueryService permitQueryService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.permitQueryService = permitQueryService;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> variationParams = this.constructParams(taskPayload, request.getAccountId());
        templateParams.getParams().putAll(variationParams);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_ACCEPTED,
            templateParams,
            "permit_variation_approved.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_VARIATION_ACCEPTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW,RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW);
    }
    
    private Map<String, Object> constructParams(final PermitVariationApplicationReviewRequestTaskPayload taskPayload,
                                               final Long accountId) {

        final PermitVariationGrantDetermination determination =
            (PermitVariationGrantDetermination) taskPayload.getDetermination();
        final PermitAcceptedVariationDecisionDetails variationDecisionDetails =
            (PermitAcceptedVariationDecisionDetails) taskPayload.getPermitVariationDetailsReviewDecision().getDetails();

        final TreeMap<PermitReviewGroup, PermitVariationReviewDecision> sortedDecisions = new TreeMap<>(
            taskPayload.getReviewGroupDecisions()
        );
        final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
            .values()
            .stream()
            .filter(
                permitVariationReviewDecision -> permitVariationReviewDecision.getType() == ReviewDecisionType.ACCEPTED)
            .map(PermitVariationReviewDecision::getDetails)
            .map(PermitAcceptedVariationDecisionDetails.class::cast)
            .map(PermitAcceptedVariationDecisionDetails::getVariationScheduleItems)
            .flatMap(List::stream)
            .toList();

        final int newConsolidationNumber = permitQueryService.getPermitConsolidationNumberByAccountId(accountId) + 1;

        return Map.of(
            "permitConsolidationNumber", newConsolidationNumber,
            "activationDate", determination.getActivationDate(),
            "variationScheduleItems", Stream.concat(
                variationDecisionDetails.getVariationScheduleItems().stream(),
                reviewGroupsVariationScheduleItems.stream()
            ).toList()
        );
    }
}
