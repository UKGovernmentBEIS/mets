package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproach;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Component
public class AviationDreUkEtsSubmittedDocumentTemplateWorkflowParamsProvider
    implements DocumentTemplateWorkflowParamsProvider<AviationDreUkEtsRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.AVIATION_DRE_SUBMIT;
    }

    @Override
    public Map<String, Object> constructParams(AviationDreUkEtsRequestPayload payload, String requestId) {
        AviationDre dre = payload.getDre();
        Year reportingYear = payload.getReportingYear();

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
            case VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER -> String.format(dreDeterminationReasonType.getDescription(), reportingYear.plusYears(1));
            case CORRECTING_NON_MATERIAL_MISSTATEMENT -> String.format(dreDeterminationReasonType.getDescription(), reportingYear);
            case IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER -> dreDeterminationReasonType.getDescription();
        };
    }
}
