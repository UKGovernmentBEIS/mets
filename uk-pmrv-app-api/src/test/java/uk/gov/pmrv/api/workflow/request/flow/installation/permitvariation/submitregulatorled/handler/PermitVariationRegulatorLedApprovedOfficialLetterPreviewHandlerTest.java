package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

@ExtendWith(MockitoExtension.class)
class PermitVariationRegulatorLedApprovedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitVariationRegulatorLedApprovedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

    @Test
    void initializePayload() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final String reqId = "reqId";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().id(reqId).accountId(accountId).build();
        final LocalDate activationDate = LocalDate.of(2021, 2, 2);
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
                .determination(PermitVariationRegulatorLedGrantDetermination.builder()
                    .type(DeterminationType.GRANTED)
                    .reason("Reason")
                    .reasonTemplate(PermitVariationReasonTemplate.OTHER)
                    .reasonTemplateOtherSummary("reasonTemplateOtherSummary")
                    .activationDate(activationDate)
                    .build())
                .permitVariationDetails(PermitVariationDetails.builder()
                		.reason("reason")
                		.build())
                .permitVariationDetailsReviewDecision(PermitAcceptedVariationDecisionDetails.builder()
                    .variationScheduleItems(List.of("sch_var_details_1", "sch_var_details_2"))
                    .notes("notes")
                    .build()
                ).reviewGroupDecisions(Map.of(
                    PermitReviewGroup.ADDITIONAL_INFORMATION,
                    PermitAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build(),
                    PermitReviewGroup.INSTALLATION_DETAILS,
                    PermitAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_inst_details_1")).build())
                )
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(taskPayload)
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> variationParams = Map.of(
            "permitConsolidationNumber", 3,
            "activationDate", activationDate,
            "variationScheduleItems", List.of("sch_var_details_1", "sch_var_details_2",
                "sch_inst_details_1",
                "sch_add_inf_1", "sch_add_inf_2"),
            "detailsReason", "reason",
            "reasonTemplate", PermitVariationReasonTemplate.OTHER,
            "feeRequired", true,
            "reasonTemplateOther", "reasonTemplateOtherSummary"
        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder().params(variationParams).build();

        when(permitQueryService.getPermitConsolidationNumberByAccountId(accountId)).thenReturn(2);
        when(paymentDetermineAmountServiceFacade.resolveAmount(reqId)).thenReturn(new BigDecimal(100));
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED,
            templateParamsWithCustom,
            "permit_ca_variation_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(variationParams);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_REGULATOR_LED_APPROVED,
            templateParamsWithCustom,
            "permit_ca_variation_approved.pdf");
    }
}
