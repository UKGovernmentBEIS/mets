package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReason;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpVariationUkEtsRegulatorLedApprovedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private EmpVariationUkEtsRegulatorLedApprovedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;

    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final String reqId = "reqId";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().id(reqId).accountId(accountId).build();
        final EmpVariationUkEtsRegulatorLedReason reason = EmpVariationUkEtsRegulatorLedReason.builder().build();
        final String operatorName = "operatorName";
        final EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload.builder()
                .reasonRegulatorLed(reason)
                .reviewGroupDecisions(Map.of(
                    EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
                    EmpAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build(),
                    EmpUkEtsReviewGroup.EMISSION_SOURCES,
                    EmpAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_inst_details_1")).build())
                )
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                    .operatorDetails(EmpOperatorDetails.builder().operatorName(operatorName).build())
                    .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(taskPayload)
            .build();
        final TemplateParams templateParams = TemplateParams.builder().accountParams(
            AviationAccountTemplateParams.builder().build()
        ).build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> variationParams = Map.of(
            "consolidationNumber", 3,
            "variationScheduleItems", List.of(
                "sch_inst_details_1",
                "sch_add_inf_1", 
                "sch_add_inf_2"),
            "reason", reason
        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder()
            .accountParams(AviationAccountTemplateParams.builder().name(operatorName).build())
            .params(variationParams).build();

        when(empQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId)).thenReturn(2);
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED,
            templateParamsWithCustom,
            "emp_ca_variation_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(variationParams);
        assertThat(templateParams.getAccountParams().getName()).isEqualTo(operatorName);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_UKETS_REGULATOR_LED_APPROVED,
            templateParamsWithCustom,
            "emp_ca_variation_approved.pdf");
    }
}
