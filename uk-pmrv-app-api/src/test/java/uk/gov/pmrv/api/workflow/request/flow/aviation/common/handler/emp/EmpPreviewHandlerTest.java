package uk.gov.pmrv.api.workflow.request.flow.aviation.common.handler.emp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpIssuanceUkEtsPreviewEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpPreviewHandlerTest {

    private final long taskId;
    private final DecisionNotification decisionNotification;
    private final FileDTO fileDTO;

    @InjectMocks
    private EmpPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private EmpIssuanceUkEtsPreviewEmpDocumentService empIssuanceUkEtsPreviewEmpDocumentService;

    @Spy
    private ArrayList<EmpPreviewDocumentService> previewDocumentServices;

    public EmpPreviewHandlerTest() {
        taskId = 1L;
        decisionNotification = DecisionNotification.builder().build();
        fileDTO = FileDTO.builder().fileName("fileName").build();
    }


    @BeforeEach
    public void setUp() {
        previewDocumentServices.add(empIssuanceUkEtsPreviewEmpDocumentService);
    }


    @Test
    void generateDocument() {
        when(empIssuanceUkEtsPreviewEmpDocumentService.getTypes()).thenReturn(List.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW));
        when(requestTaskService.findTaskById(taskId)).thenReturn(RequestTask.builder().type(
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW).build()
        );
        when(empIssuanceUkEtsPreviewEmpDocumentService.create(taskId, decisionNotification)).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);
        
        verify(empIssuanceUkEtsPreviewEmpDocumentService, times(1)).create(taskId, decisionNotification);
    }


    @Test
    void generateDocument_previewDocumentServiceDoesntExist_throwException() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            when(requestTaskService.findTaskById(taskId)).thenReturn(RequestTask.builder().type(RequestTaskType.EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS).build());
            handler.generateDocument(taskId, decisionNotification);
        });

        String actualCode = exception.getErrorCode().getCode();
        assertEquals(actualCode, MetsErrorCode.INVALID_DOCUMENT_TEMPLATE_FOR_REQUEST_TASK.getCode());
        assertTrue(Arrays.asList(exception.getData()).contains(RequestTaskType.EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS));
    }

    @Test
    void getTypes() {
        assertIterableEquals(List.of(
                DocumentTemplateType.EMP_UKETS,
                DocumentTemplateType.EMP_CORSIA
        ),handler.getTypes());
    }

    @Test
    void getTaskTypes() {
        assertIterableEquals(List.of(
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW,
                RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
                RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW,
                RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT,
                RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW,
                RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW,
                RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT
        ),handler.getTaskTypes());
    }
}
