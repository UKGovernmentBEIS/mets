package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;


@Service
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingSubmittedDocumentTemplateWorkflowParamsProvider
        implements DocumentTemplateWorkflowParamsProvider<AviationAerCorsia3YearPeriodOffsettingRequestPayload> {


    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.AVIATION_AER_CORSIA_3_YEAR_PERIOD_OFFSETTING_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(AviationAerCorsia3YearPeriodOffsettingRequestPayload payload,
                                               String requestId) {

        return constructParamsFromAviationAerCorsia3YearPeriodOffsetting(
                payload.getAviationAerCorsia3YearPeriodOffsetting());
    }


    public Map<String, Object> constructParamsFromSubmitRequestTaskPayload(
            AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload payload,
            AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload,
            String requestId) {

        return constructParamsFromAviationAerCorsia3YearPeriodOffsetting(
                payload.getAviationAerCorsia3YearPeriodOffsetting());
    }

    private Map<String, Object> constructParamsFromAviationAerCorsia3YearPeriodOffsetting(AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting) {

        Map<String,Object> params = new HashMap<>();

        List<Year> schemeYears = aviationAerCorsia3YearPeriodOffsetting.getSchemeYears();

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData =
                aviationAerCorsia3YearPeriodOffsetting.getYearlyOffsettingData();

        AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData totalYearlyOffsettingData =
                aviationAerCorsia3YearPeriodOffsetting.getTotalYearlyOffsettingData();

        params.put("schemeYear1", schemeYears.getFirst());
        params.put("schemeYear2", schemeYears.get(1));
        params.put("schemeYear3", schemeYears.get(2));
        params.put("lastYearOfThePeriodPlusOne", schemeYears.get(2).plusYears(1));
        params.put("lastYearOfThePeriodPlusTwo", schemeYears.get(2).plusYears(2));

        params.put("calculatedAnnualOffsettingYear1", yearlyOffsettingData.get(schemeYears.getFirst()).getCalculatedAnnualOffsetting());
        params.put("calculatedAnnualOffsettingYear2", yearlyOffsettingData.get(schemeYears.get(1)).getCalculatedAnnualOffsetting());
        params.put("calculatedAnnualOffsettingYear3", yearlyOffsettingData.get(schemeYears.get(2)).getCalculatedAnnualOffsetting());


        params.put("cefEmissionsReductionsYear1", yearlyOffsettingData.get(schemeYears.getFirst()).getCefEmissionsReductions());
        params.put("cefEmissionsReductionsYear2", yearlyOffsettingData.get(schemeYears.get(1)).getCefEmissionsReductions());
        params.put("cefEmissionsReductionsYear3", yearlyOffsettingData.get(schemeYears.get(2)).getCefEmissionsReductions());

        params.put("totalCalculatedAnnualOffsetting", totalYearlyOffsettingData.getCalculatedAnnualOffsetting());
        params.put("totalCefEmissionsReductions", totalYearlyOffsettingData.getCefEmissionsReductions());

        params.put("periodOffsettingRequirements", aviationAerCorsia3YearPeriodOffsetting.getPeriodOffsettingRequirements());

        params.put("operatorHasOffsettingRequirements", aviationAerCorsia3YearPeriodOffsetting.getOperatorHaveOffsettingRequirements().booleanValue());

        return params;
    }
}
