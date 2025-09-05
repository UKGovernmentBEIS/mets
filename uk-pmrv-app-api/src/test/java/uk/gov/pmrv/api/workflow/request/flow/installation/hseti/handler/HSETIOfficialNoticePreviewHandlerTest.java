package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETICompletedDocumentTemplateWorkflowParamsProvider;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIOfficialNoticePreviewHandlerTest {

    @InjectMocks
    private HSETIOfficialNoticePreviewHandler handler;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private HSETICompletedDocumentTemplateWorkflowParamsProvider paramsProvider;


    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void generateDocument(){
        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();

        final Request request = Request.builder()
                .type(RequestType.HSE_TI)
                .payload(HSETIRequestPayload.builder()
                        .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                        .build())
                .build();


        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                        .builder()
                        .build())
                .build();
        final TemplateParams templateParams = TemplateParams.builder()
                .accountParams(InstallationAccountTemplateParams.builder().build())
                .build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> params = Map.of();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED,
                templateParams,
                "letter_preview.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(params);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.HSE_TI_COMPLETED,
                templateParams,
                "letter_preview.pdf");

    }

    @Test
    void getTypes(){
        assertThat(handler.getTypes()).containsExactlyInAnyOrder(DocumentTemplateType.HSE_TI_COMPLETED);
    }

    @Test
    void getTaskTypes(){
         assertThat(handler.getTaskTypes()).containsExactlyInAnyOrder(
                 RequestTaskType.HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT, RequestTaskType.HSE_TI_APPLICATION_PEER_REVIEW);
    }
}
