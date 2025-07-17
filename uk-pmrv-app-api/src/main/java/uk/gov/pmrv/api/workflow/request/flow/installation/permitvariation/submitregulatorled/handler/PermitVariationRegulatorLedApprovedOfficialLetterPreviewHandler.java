package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

@Service
public class PermitVariationRegulatorLedApprovedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final PermitQueryService permitQueryService;
    private final PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;
    
    public PermitVariationRegulatorLedApprovedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                                           final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                                           final DocumentFileGeneratorService documentFileGeneratorService,
                                                                           final PermitQueryService permitQueryService,
                                                                           final PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
        this.permitQueryService = permitQueryService;
        this.paymentDetermineAmountServiceFacade = paymentDetermineAmountServiceFacade;
    }

    @Override
    protected FileDTO generateDocument(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> variationParams = this.constructParams(taskPayload, request.getAccountId(), request.getId());
        templateParams.getParams().putAll(variationParams);

        return documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED,
            templateParams,
            "permit_ca_variation_approved.pdf"
        );
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT);
    }
    
    private Map<String, Object> constructParams(final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload,
                                               final Long accountId,
                                               final String requestId) {

        final PermitVariationRegulatorLedGrantDetermination determination = taskPayload.getDetermination();
        final PermitAcceptedVariationDecisionDetails variationDecisionDetails = taskPayload.getPermitVariationDetailsReviewDecision();

        final TreeMap<PermitReviewGroup, PermitAcceptedVariationDecisionDetails> sortedDecisions = new TreeMap<>(
            taskPayload.getReviewGroupDecisions()
        );
        final List<String> reviewGroupsVariationScheduleItems = sortedDecisions
            .values()
            .stream()
            .map(PermitAcceptedVariationDecisionDetails::getVariationScheduleItems)
            .flatMap(List::stream)
            .toList();
        final int newConsolidationNumber = permitQueryService.getPermitConsolidationNumberByAccountId(accountId) + 1;
        final boolean feeRequired = paymentDetermineAmountServiceFacade.resolveAmount(requestId).compareTo(BigDecimal.ZERO) > 0;

        final Map<String, Object> params = new HashMap<>();
        params.put("permitConsolidationNumber", newConsolidationNumber);
        params.put("activationDate", determination.getActivationDate());
        params.put("variationScheduleItems", Stream.concat(
        	variationDecisionDetails != null ? variationDecisionDetails.getVariationScheduleItems().stream() : Collections.emptyList().stream(),
            reviewGroupsVariationScheduleItems.stream()
        ).toList());
        params.put("detailsReason", taskPayload.getPermitVariationDetails().getReason());
        params.put("reasonTemplate", determination.getReasonTemplate());
        params.put("reasonTemplateOther", determination.getReasonTemplateOtherSummary());
        params.put("feeRequired", feeRequired);

        return params;
    }
}
