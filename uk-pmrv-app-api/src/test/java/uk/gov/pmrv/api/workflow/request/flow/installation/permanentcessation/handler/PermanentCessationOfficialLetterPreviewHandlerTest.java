package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationScope;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermanentCessationOfficialLetterPreviewHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService installationPreviewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @InjectMocks
    private PermanentCessationOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTask requestTask;

    @Mock
    private Request request;

    @Mock
    private FileDTO fileDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new PermanentCessationOfficialLetterPreviewHandler(
                requestTaskService,
                installationPreviewOfficialNoticeService,
                documentFileGeneratorService
        );
    }

    @Test
    void getTaskTypes_shouldReturnCorrectType() {
        List<RequestTaskType> types = handler.getTaskTypes();
        assertThat(types).containsExactly(
                RequestTaskType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW,
                RequestTaskType.PERMANENT_CESSATION_APPLICATION_SUBMIT);
    }

    @Test
    void getTypes_shouldReturnCorrectTemplateType() {
        List<DocumentTemplateType> types = handler.getTypes();
        assertThat(types).containsExactly(
                DocumentTemplateType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW,
                DocumentTemplateType.PERMANENT_CESSATION_APPLICATION_SUBMIT);
    }

    @Test
    void generateDocument_shouldAddAdditionalInformation() {
        Long taskId = 123L;
        String additionalDetails = "Some extra info";
        DecisionNotification decisionNotification = new DecisionNotification();

        PermanentCessation permanentCessation = mock(PermanentCessation.class);
        when(permanentCessation.getAdditionalDetails()).thenReturn(additionalDetails);
        when(permanentCessation.getCessationDate()).thenReturn(LocalDate.of(2025,1, 1));
        when(permanentCessation.getCessationScope()).thenReturn(PermanentCessationScope.WHOLE_INSTALLATION);

        PermanentCessationApplicationSubmitRequestTaskPayload payload =
                mock(PermanentCessationApplicationSubmitRequestTaskPayload.class);
        when(payload.getPermanentCessation()).thenReturn(permanentCessation);

        when(requestTask.getPayload()).thenReturn(payload);
        when(requestTask.getRequest()).thenReturn(request);
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        TemplateParams templateParams = new TemplateParams();
        templateParams.setParams(new HashMap<>());

        when(installationPreviewOfficialNoticeService.generateCommonParamsWithExtraAccountDetails(request, decisionNotification))
                .thenReturn(templateParams);

        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMANENT_CESSATION, templateParams, "Permanent_cessation_notice_preview.pdf"))
                .thenReturn(fileDTO);

        FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertThat(result).isEqualTo(fileDTO);
        assertThat(templateParams.getParams())
                .containsEntry("additionalInformation", additionalDetails);
        assertThat(templateParams.getParams())
                .containsEntry("cessationDate", "1 Jan 2025");
    }

    @Test
    void generateDocument_shouldAddEmptyAdditionalInformation_whenMissing() {
        Long taskId = 123L;
        DecisionNotification decisionNotification = new DecisionNotification();

        PermanentCessationApplicationSubmitRequestTaskPayload payload = new PermanentCessationApplicationSubmitRequestTaskPayload();
        payload.setPermanentCessation(PermanentCessation.builder()
                        .cessationDate(LocalDate.of(2025, 1, 1))
                .cessationScope(PermanentCessationScope.WHOLE_INSTALLATION).build());

        when(requestTask.getPayload()).thenReturn(payload); // Simulate missing payload
        when(requestTask.getRequest()).thenReturn(request);
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        TemplateParams templateParams = new TemplateParams();
        templateParams.setParams(new HashMap<>());

        when(installationPreviewOfficialNoticeService.generateCommonParamsWithExtraAccountDetails(request, decisionNotification))
                .thenReturn(templateParams);

        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMANENT_CESSATION, templateParams, "Permanent_cessation_notice_preview.pdf"))
                .thenReturn(fileDTO);

        FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertThat(result).isEqualTo(fileDTO);
        assertThat(templateParams.getParams())
                .containsEntry("additionalInformation", "");
        assertThat(templateParams.getParams())
                .containsEntry("isWholeInstallation", true);
        assertThat(templateParams.getParams())
                .containsEntry("cessationDate", "1 Jan 2025");
    }
}
