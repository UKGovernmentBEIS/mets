package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AviationVirPreviewHandlerTest {

    @Mock
    private AviationVirOfficialNoticeService aviationVirOfficialNoticeService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @InjectMocks
    private AviationVirPreviewHandler aviationVirPreviewHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateDocument() {
        Long taskId = 1L;
        DecisionNotification decisionNotification = new DecisionNotification();
        RequestTask requestTask = mock(RequestTask.class);
        Request request = mock(Request.class);
        AviationVirRequestPayload requestPayload = new AviationVirRequestPayload();
        AviationVirApplicationReviewRequestTaskPayload taskPayload = new AviationVirApplicationReviewRequestTaskPayload();
        FileDTO fileDTO = new FileDTO();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(requestTask.getRequest()).thenReturn(request);
        when(request.getId()).thenReturn("1L");
        when(requestService.findRequestById("1L")).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(aviationVirOfficialNoticeService.doGenerateOfficialNoticeWithoutSave(request)).thenReturn(fileDTO);

        FileDTO result = aviationVirPreviewHandler.generateDocument(taskId, decisionNotification);

        assertEquals(fileDTO, result);
        verify(requestTaskService).findTaskById(taskId);
        verify(requestService).findRequestById("1L");
        verify(aviationVirOfficialNoticeService).doGenerateOfficialNoticeWithoutSave(request);
    }

    @Test
    void getTypes() {
        List<DocumentTemplateType> types = aviationVirPreviewHandler.getTypes();
        assertEquals(1, types.size());
        assertEquals(DocumentTemplateType.AVIATION_VIR_REVIEWED, types.get(0));
    }

    @Test
    void getTaskTypes() {
        List<RequestTaskType> taskTypes = aviationVirPreviewHandler.getTaskTypes();
        assertEquals(1, taskTypes.size());
        assertEquals(RequestTaskType.AVIATION_VIR_APPLICATION_REVIEW, taskTypes.get(0));
    }
}
