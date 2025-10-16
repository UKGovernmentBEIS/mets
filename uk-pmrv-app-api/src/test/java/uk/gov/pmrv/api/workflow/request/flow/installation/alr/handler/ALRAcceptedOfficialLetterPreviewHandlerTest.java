package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRAcceptedOfficialLetterPreviewHandlerTest {


    @InjectMocks
    private ALRAcceptedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generateDocument() {
        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();

        final ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome = ALRApplicationRegulatorReviewOutcome.builder()
                .build();

        final ALRGrantAuthorityResponse authorityResponse = ALRGrantAuthorityResponse.builder()
                .type(DoalAuthorityResponseType.VALID)
                .build();

        final ALRRequestPayload requestPayload = ALRRequestPayload.builder()
                .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                .reportingYear(Year.of(2023))
                .regulatorReviewOutcome(regulatorReviewOutcome)
                .authorityReviewOutcome(
                        ALRApplicationAuthorityReviewOutcome.builder()
                                .authorityResponse(authorityResponse)
                                .build()
                )
                .build();

        final Request request = Request.builder()
                .type(RequestType.ALR)
                .payload(requestPayload)
                .build();

        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload =
                ALRAuthorityResponseSubmitRequestTaskPayload.builder()
                        .authorityReviewOutcome(
                                ALRApplicationAuthorityReviewOutcome.builder()
                                        .authorityResponse(authorityResponse)
                                        .build()
                        )
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> expectedParams = Map.of(
                "reportingYear", Year.of(2023),
                "alr", regulatorReviewOutcome,
                "authorityResponse", authorityResponse
        );

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification))
                .thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.ALR_ACCEPTED,
                templateParams,
                "Activity_level_report_approved_by_Authority_notice.pdf"))
                .thenReturn(fileDTO);

        // Invoke
        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        // Verify
        Assertions.assertEquals(fileDTO, result);
        assertThat(templateParams.getParams())
                .containsExactlyInAnyOrderEntriesOf(expectedParams);

        verify(requestTaskService).findTaskById(taskId);
        verify(previewOfficialNoticeService).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService).generateFileDocument(
                DocumentTemplateType.ALR_ACCEPTED,
                templateParams,
                "Activity_level_report_approved_by_Authority_notice.pdf");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(DocumentTemplateType.ALR_ACCEPTED);
    }

    @Test
    void getTaskTypes() {
        assertThat(handler.getTaskTypes())
                .containsExactly(RequestTaskType.ALR_AUTHORITY_RESPONSE_SUBMIT);
    }
}
