package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.FollowUpAction;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.FollowUpActionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditSubmitOfficialNoticePreviewHandlerTest {

    @InjectMocks
    private InstallationAuditSubmitOfficialNoticePreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private InstallationAuditSubmittedDocumentTemplateWorkflowParamsProvider installationAuditSubmittedDocumentTemplateWorkflowParamsProvider;


    @Test
    void getTypes(){
        assertThat(handler.getTypes()).containsExactlyInAnyOrder(DocumentTemplateType.INSTALLATION_AUDIT_SUBMITTED);
    }

    @Test
    void getTaskTypes(){
         assertThat(handler.getTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.INSTALLATION_AUDIT_APPLICATION_SUBMIT,RequestTaskType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW);
    }

    @Test
    void generateDocument(){
        final Long taskId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();

        FollowUpAction followUpAction = FollowUpAction
                .builder()
                .followUpActionType(FollowUpActionType.MISSTATEMENT)
                .explanation("test")
                .build();

        List<FollowUpAction> followUpActions = List.of(followUpAction);

        final InstallationInspection installationInspection = InstallationInspection
                .builder()
                .followUpActions(followUpActions)
                .responseDeadline(LocalDate.now().plusDays(10))
                .build();

        final Request request = Request.builder()
                .type(RequestType.INSTALLATION_AUDIT)
                .payload(InstallationAuditRequestPayload.builder()
                        .payloadType(RequestPayloadType.INSTALLATION_AUDIT_REQUEST_PAYLOAD)
                        .installationInspection(installationInspection)
                        .build())
                .build();


        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(InstallationInspectionApplicationSubmitRequestTaskPayload.builder()
                        .installationInspection(installationInspection)
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
                DocumentTemplateType.INSTALLATION_AUDIT_SUBMITTED,
                templateParams,
                "installation_audit_official_letter_preview.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(params);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.INSTALLATION_AUDIT_SUBMITTED,
                templateParams,
                "installation_audit_official_letter_preview.pdf");

    }
}
