package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.notification.PermitRevocationCessationDocumentTemplateWorkflowParamsProvider;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermitRevocationCessationNotifyOperatorOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitRevocationCessationNotifyOperatorOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private PermitRevocationCessationDocumentTemplateWorkflowParamsProvider workflowParamsProvider;


    @Test
    void initializePayload() {

        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
                .permitCessationCompletedDate(LocalDate.now()).build();
        final PermitCessationSubmitRequestTaskPayload requestTaskPayload = PermitCessationSubmitRequestTaskPayload.builder().cessationContainer(PermitCessationContainer.builder().build()).build();
        final Request request = Request.builder().payload(requestPayload).build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(requestTaskPayload)
                .request(request)
                .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParamsWithExtraAccountDetails(request, decisionNotification)).thenReturn(templateParams);
        when(workflowParamsProvider.constructParams((PermitRevocationRequestPayload)request.getPayload(), null)).thenReturn(Map.of());
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_REVOCATION_CESSATION,
                templateParams,
                "permit_revocation_cessation_notice.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParamsWithExtraAccountDetails(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.PERMIT_REVOCATION_CESSATION,
                templateParams,
                "permit_revocation_cessation_notice.pdf");
        verify(workflowParamsProvider, times(1)).constructParams((PermitRevocationRequestPayload)request.getPayload(), null);
    }
}
