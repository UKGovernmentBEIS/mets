package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmitOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private AviationDoECorsiaSubmitOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AviationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private AviationDoECorsiaSubmittedDocumentTemplateWorkflowParamsProvider templateWorkflowParamsProvider;

    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final String reqId = "reqId";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder()
                .reportingYear(Year.of(2022))
                .build();
        final Request request = Request.builder()
                .id(reqId)
                .accountId(accountId)
                .payload(requestPayload)
                .build();

        final AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload =
                AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD)
                        .doe(AviationDoECorsia.builder()
                                .emissions(AviationDoECorsiaEmissions.builder()
                                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TEN)
                                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                                        .calculationApproach("calc").build())
                                .fee(AviationDoECorsiaFee.builder().build())
                                .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                                        .type(
                                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                                        .furtherDetails("furtherDetails")
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
                "InternationalEmissions", "2",
                "offsettingEmissions", "1",
                "CEFEmissions", "10",
                "determinationReason", AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT.toString(),
                "calculationApproach", "calc"
        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder()
                .accountParams(AviationAccountTemplateParams.builder().build())
                .params(params).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification, false)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.AVIATION_DOE_SUBMITTED,
                templateParamsWithCustom,
                "DoE_notice.pdf")).thenReturn(fileDTO);
        when(templateWorkflowParamsProvider.constructParams(taskPayload, Year.of(2022),accountId)).thenReturn(params);


        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(params);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification, false);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.AVIATION_DOE_SUBMITTED,
                templateParamsWithCustom,
                "DoE_notice.pdf");
    }

    @Test
    void getTypes(){
        assertThat(handler.getTypes()).containsExactlyInAnyOrder(DocumentTemplateType.AVIATION_DOE_SUBMITTED);
    }
}
