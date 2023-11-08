package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproach;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AviationDreUkEtsSubmittedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Test
    void constructParams() {
        Year reportingYear = Year.of(2022);
        BigDecimal emissions = BigDecimal.valueOf(2520.23);
        AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder()
            .payloadType(RequestPayloadType.AVIATION_DRE_UKETS_REQUEST_PAYLOAD)
            .reportingYear(reportingYear)
            .dre(AviationDre.builder()
                .determinationReason(AviationDreDeterminationReason.builder()
                    .furtherDetails("details")
                    .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                    .build())
                .totalReportableEmissions(emissions)
                .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                    .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                    .build())
                .build())
            .build();

        Map<String, Object> resultMap = paramsProvider.constructParams(requestPayload, "1L");

        assertThat(resultMap).containsExactlyInAnyOrderEntriesOf(Map.of(
            "reportingYear", reportingYear,
            "totalReportableEmissions", emissions,
            "determinationReasonDescription", String.format(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT.getDescription(), reportingYear),
            "emissionsCalculationApproachDescription", AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY.getDescription()
        ));
    }

    @Test
    void getContextActionType() {
        assertEquals(DocumentTemplateGenerationContextActionType.AVIATION_DRE_SUBMIT,paramsProvider.getContextActionType());
    }
}