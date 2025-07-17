package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproach;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AviationDreUkEtsSubmitOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private final AviationPreviewOfficialNoticeService previewOfficialNoticeService;
    private final DocumentFileGeneratorService documentFileGeneratorService;

    public AviationDreUkEtsSubmitOfficialLetterPreviewHandler(final RequestTaskService requestTaskService,
                                                              final AviationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                              final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.previewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final AviationDreUkEtsApplicationSubmitRequestTaskPayload taskPayload =
                (AviationDreUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();

        final TemplateParams templateParams = previewOfficialNoticeService.generateCommonParams(request, decisionNotification, false);
        final Map<String, Object> params = this.constructParams(taskPayload, requestPayload);
        templateParams.getParams().putAll(params);

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.AVIATION_DRE_SUBMITTED,
                templateParams,
                "DRE_notice.pdf"
        );
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(DocumentTemplateType.AVIATION_DRE_SUBMITTED);
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT,RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW);
    }

    private Map<String, Object> constructParams(AviationDreUkEtsApplicationSubmitRequestTaskPayload taskPayload, AviationDreUkEtsRequestPayload requestPayload) {
        AviationDre dre = taskPayload.getDre();
        Year reportingYear = requestPayload.getReportingYear();

        Map<String, Object> vars = new HashMap<>();

        vars.put("reportingYear", reportingYear);
        vars.put("totalReportableEmissions", dre.getTotalReportableEmissions());
        vars.put("determinationReasonDescription",
                retrieveDeterminationReasonDescription(dre.getDeterminationReason().getType(), reportingYear));

        AviationDreEmissionsCalculationApproach dreEmissionsCalculationApproach = dre.getCalculationApproach();
        vars.put("emissionsCalculationApproachDescription",
                dreEmissionsCalculationApproach.getType() == AviationDreEmissionsCalculationApproachType.OTHER_DATASOURCE
                        ? dreEmissionsCalculationApproach.getOtherDataSourceExplanation()
                        : dreEmissionsCalculationApproach.getType().getDescription());

        return vars;
    }

    private String retrieveDeterminationReasonDescription(AviationDreDeterminationReasonType dreDeterminationReasonType,
                                                          Year reportingYear) {
        return switch (dreDeterminationReasonType) {
            case VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER ->
                    String.format(dreDeterminationReasonType.getDescription(), reportingYear.plusYears(1));
            case CORRECTING_NON_MATERIAL_MISSTATEMENT ->
                    String.format(dreDeterminationReasonType.getDescription(), reportingYear);
            case IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER ->
                    dreDeterminationReasonType.getDescription();
        };
    }
}
