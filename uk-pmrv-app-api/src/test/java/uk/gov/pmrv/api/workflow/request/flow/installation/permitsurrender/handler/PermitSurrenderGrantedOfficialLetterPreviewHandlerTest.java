package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.handler;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermitSurrenderGrantedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitSurrenderGrantedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider workflowParamsProvider;


    @Test
    void initializePayload() {

        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParamsWithExtraAccountDetails(request, decisionNotification)).thenReturn(templateParams);
        when(workflowParamsProvider.constructParams((PermitSurrenderRequestPayload)request.getPayload(), null)).thenReturn(Map.of());
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_SURRENDERED_NOTICE,
                templateParams,
                "permit_surrender_notice.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParamsWithExtraAccountDetails(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.PERMIT_SURRENDERED_NOTICE,
                templateParams,
                "permit_surrender_notice.pdf");
        verify(workflowParamsProvider, times(1)).constructParams((PermitSurrenderRequestPayload)request.getPayload(), null);
    }
}
