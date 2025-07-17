package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

@ExtendWith(MockitoExtension.class)
class PermitVariationGrantedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitVariationGrantedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;
    
    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void initializePayload() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().accountId(accountId).build();
        final LocalDate activationDate = LocalDate.of(2021, 2, 2);
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
            .determination(PermitVariationGrantDetermination.builder()
                .type(DeterminationType.GRANTED)
                .reason("Reason")
                .activationDate(activationDate)
                .build())
            .permitVariationDetailsReviewDecision(PermitVariationReviewDecision.builder()
                .details(PermitAcceptedVariationDecisionDetails.builder()
                    .variationScheduleItems(List.of("sch_var_details_1", "sch_var_details_2"))
                    .notes("notes")
                    .build())
                .build())
            .reviewGroupDecisions(Map.of(
                PermitReviewGroup.ADDITIONAL_INFORMATION, PermitVariationReviewDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .details(PermitAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_add_inf_1", "sch_add_inf_2")).build()).build(),
                PermitReviewGroup.INSTALLATION_DETAILS, PermitVariationReviewDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .details(PermitAcceptedVariationDecisionDetails.builder()
                        .variationScheduleItems(List.of("sch_inst_details_1")).build()).build(),
                PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitVariationReviewDecision.builder()
                    .type(ReviewDecisionType.REJECTED)
                    .details(ReviewDecisionDetails.builder().notes("notes").build()).build()
            ))
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
                "sch_add_inf_1", "sch_add_inf_2")
        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder().params(variationParams).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(permitQueryService.getPermitConsolidationNumberByAccountId(accountId)).thenReturn(2);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_ACCEPTED,
            templateParamsWithCustom,
            "permit_variation_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);        
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(variationParams);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_VARIATION_ACCEPTED,
            templateParamsWithCustom,
            "permit_variation_approved.pdf");
    }
}
