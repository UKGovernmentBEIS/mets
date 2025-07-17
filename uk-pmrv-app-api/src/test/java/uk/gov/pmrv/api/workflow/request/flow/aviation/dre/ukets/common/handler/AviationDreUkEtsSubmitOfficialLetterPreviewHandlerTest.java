package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.AviationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproach;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.domain.AviationDreUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsSubmitOfficialLetterPreviewHandlerTest {


    @InjectMocks
    private AviationDreUkEtsSubmitOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final String reqId = "reqId";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final AviationDreUkEtsRequestPayload requestPayload = AviationDreUkEtsRequestPayload.builder()
                .reportingYear(Year.of(2022))
                .build();
        final Request request = Request.builder()
                .id(reqId)
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final AviationDreUkEtsApplicationSubmitRequestTaskPayload taskPayload =
                AviationDreUkEtsApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                        .dre(AviationDre.builder()
                                .determinationReason(AviationDreDeterminationReason.builder()
                                        .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                                        .furtherDetails("Further details")
                                        .build())
                                .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                                        .type(AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                        .build())
                                .totalReportableEmissions(BigDecimal.TEN)
                                .fee(AviationDreFee.builder()
                                        .chargeOperator(true)
                                        .feeDetails(AviationDreFeeDetails.builder()
                                                .hourlyRate(BigDecimal.ONE)
                                                .totalBillableHours(BigDecimal.TEN)
                                                .dueDate(LocalDate.of(2023, 12,5))
                                                .comments("comments")
                                                .build())
                                        .build())
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
        final Map<String, Object> params = Map.of(
                "reportingYear", Year.of(2022),
                "totalReportableEmissions", BigDecimal.TEN,
                "determinationReasonDescription", String.format(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT.getDescription(), Year.of(2022)),
                "emissionsCalculationApproachDescription", AviationDreEmissionsCalculationApproachType.EUROCONTROL_SUPPORT_FACILITY.getDescription()
        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder()
                .accountParams(AviationAccountTemplateParams.builder().build())
                .params(params).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification, false)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.AVIATION_DRE_SUBMITTED,
                templateParamsWithCustom,
                "DRE_notice.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(params);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification, false);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.AVIATION_DRE_SUBMITTED,
                templateParamsWithCustom,
                "DRE_notice.pdf");
    }

    @Test
    void getTypes(){
        assertThat(handler.getTypes()).containsExactlyInAnyOrder(DocumentTemplateType.AVIATION_DRE_SUBMITTED);
    }
}
