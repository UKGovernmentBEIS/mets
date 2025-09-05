package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonItemType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Set;


@Service
public class ALRAcceptedOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public ALRAcceptedOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                    final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                    final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload =
                (ALRAuthorityResponseSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification);

        final Map<String, Object> params = this.constructParams(taskPayload, requestPayload);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.DOAL_ACCEPTED, //TODO: Change when alr template arrives
                templateParams,
                "Activity_level_determination_approved_by_Authority_notice.pdf");
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.ALR_ACCEPTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.ALR_AUTHORITY_RESPONSE_SUBMIT);
    }

    private Map<String, Object> constructParams(ALRAuthorityResponseSubmitRequestTaskPayload taskPayload, ALRRequestPayload requestPayload) {
        //TODO: Change when alr template arrives ~ use DUMMY data until then

        return Map.of(
                "reportingYear", Year.now(),
                "doal", Doal.builder().activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                            .preliminaryAllocations((new TreeSet<>()))
                            .activityLevels(new ArrayList<>())
                            .areConservativeEstimates(false)
                            .build())
                        .operatorActivityLevelReport(OperatorActivityLevelReport.builder().areActivityLevelsEstimated(false)
                                .comment("dummy comment").build())
                        .verificationReportOfTheActivityLevelReport(VerificationReportOfTheActivityLevelReport.builder()
                                .comment("dummy comment").build())
                        .additionalDocuments(DoalAdditionalDocuments.builder().exist(false).build())
                        .determination(DoalProceedToAuthorityDetermination.builder()
                                .needsOfficialNotice(false)
                                .hasWithholdingOfAllowances(false)
                                .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                .reason("reason")
                                .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                                .articleReasonItems(Set.of(ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5))
                            .build())
                        .build(),
                "authorityResponse", DoalGrantAuthorityResponse.builder().type(DoalAuthorityResponseType.VALID).authorityRespondDate(LocalDate.now()).build());
    }
}
