package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

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
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.EmpCorsiaOperatorDetails;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaRegulatorLedApprovedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private EmpVariationCorsiaRegulatorLedApprovedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private EmissionsMonitoringPlanQueryService empQueryService;
    
    @Mock
    private
    DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;

    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final String reqId = "reqId";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().id(reqId).accountId(accountId).build();
        final String operatorName = "operatorName";
        final String reason = "reason";
        final LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder()
            .line1("line 1")
            .build();
        final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
                .reasonRegulatorLed(reason)
                .reviewGroupDecisions(Map.of(
                    EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
                    EmpAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build(),
                    EmpCorsiaReviewGroup.EMISSION_SOURCES,
                    EmpAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_inst_details_1")).build())
                )
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                    .operatorDetails(EmpCorsiaOperatorDetails.builder()
                        .organisationStructure(IndividualOrganisation.builder()
                            .organisationLocation(location)
                            .build())
                        .operatorName(operatorName).build())
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
        final String locationString = "the location";
        final TemplateParams templateParamsWithCustom = TemplateParams.builder()
            .accountParams(AviationAccountTemplateParams.builder().location(locationString).name(operatorName).build())
            .params(variationParams).build();

        when(empQueryService.getEmissionsMonitoringPlanConsolidationNumberByAccountId(accountId)).thenReturn(2);
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParamsWithoutAccountNameLocation(request, decisionNotification)).thenReturn(templateParams);
        when(documentTemplateLocationInfoResolver.constructLocationInfo(location)).thenReturn(locationString);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED,
            templateParamsWithCustom,
            "corsia_emp_ca_variation_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(variationParams);
        assertThat(templateParams.getAccountParams().getName()).isEqualTo(operatorName);
        assertThat(templateParams.getAccountParams().getLocation()).isEqualTo(locationString);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParamsWithoutAccountNameLocation(request, decisionNotification);
        verify(documentTemplateLocationInfoResolver, times(1)).constructLocationInfo(location);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPROVED,
            templateParamsWithCustom,
            "corsia_emp_ca_variation_approved.pdf");
    }
}
