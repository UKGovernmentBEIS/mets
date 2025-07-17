package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
public class AviationAerCorsiaAnnualOffsettingSubmittedDocumentTemplateWorkflowParamsProvider
        implements DocumentTemplateWorkflowParamsProvider<AviationAerCorsiaAnnualOffsettingRequestPayload> {
    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMITTED;
    }

    @Override
    public Map<String, Object> constructParams(AviationAerCorsiaAnnualOffsettingRequestPayload payload, String requestId) {

        Map<String, Object> map = new HashMap<>();
        map.put("schemeYear", payload.getAviationAerCorsiaAnnualOffsetting().getSchemeYear().toString());
        map.put("calculatedAnnualOffsettingRequirements", payload.getAviationAerCorsiaAnnualOffsetting().getCalculatedAnnualOffsetting());
        map.put("schemeYearPlusOne", payload.getAviationAerCorsiaAnnualOffsetting().getSchemeYear().plusYears(1L));
        map.put("annualOffsettingRequirements", payload.getAviationAerCorsiaAnnualOffsetting().getTotalChapter());
        map.put("sectorGrowth", payload.getAviationAerCorsiaAnnualOffsetting().getSectorGrowth());

        return map;
    }
}
