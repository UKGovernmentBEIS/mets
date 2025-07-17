package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreOfficialNoticeGenerateService;

public class DreOfficialNoticePreviewHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private DreOfficialNoticeGenerateService dreOfficialNoticeGenerateService;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private DreOfficialNoticePreviewHandler dreOfficialNoticePreviewHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateDocument_shouldGenerateDocument() {
        Long taskId = 1L;
        DecisionNotification decisionNotification = new DecisionNotification();

        RequestTask requestTask = new RequestTask();
        Request request = new Request();
        DreRequestPayload dreRequestPayload = DreRequestPayload.builder()
                .build();
        request.setPayload(dreRequestPayload);
        DreApplicationSubmitRequestTaskPayload dreApplicationSubmitRequestTaskPayload = new DreApplicationSubmitRequestTaskPayload();
        FileDTO expectedFileDTO = new FileDTO();

        request.setId("2");
        requestTask.setRequest(request);
        requestTask.setPayload(dreApplicationSubmitRequestTaskPayload);
        dreApplicationSubmitRequestTaskPayload.setDre(new Dre());

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(requestService.findRequestById(request.getId())).thenReturn(request);
        when(dreOfficialNoticeGenerateService.doGenerateOfficialNoticeWithoutSave(request, decisionNotification)).thenReturn(expectedFileDTO);

        FileDTO actualFileDTO = dreOfficialNoticePreviewHandler.generateDocument(taskId, decisionNotification);

        verify(requestTaskService).findTaskById(taskId);
        verify(requestService).findRequestById(request.getId());
        verify(dreOfficialNoticeGenerateService).doGenerateOfficialNoticeWithoutSave(request, decisionNotification);

        assertThat(actualFileDTO).isEqualTo(expectedFileDTO);
    }

    @Test
    public void getTypes_shouldReturnDocumentTemplateTypes() {
        List<DocumentTemplateType> documentTemplateTypes = dreOfficialNoticePreviewHandler.getTypes();
        assertThat(documentTemplateTypes).containsExactly(DocumentTemplateType.DRE_SUBMITTED);
    }

    @Test
    public void getTaskTypes_shouldReturnRequestTaskTypes() {
        List<RequestTaskType> requestTaskTypes = dreOfficialNoticePreviewHandler.getTaskTypes();
        Assertions.assertIterableEquals(requestTaskTypes,List.of(RequestTaskType.DRE_APPLICATION_SUBMIT,RequestTaskType.DRE_APPLICATION_PEER_REVIEW));
    }
}
