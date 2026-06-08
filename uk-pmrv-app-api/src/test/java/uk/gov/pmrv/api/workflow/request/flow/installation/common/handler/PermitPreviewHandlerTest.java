package uk.gov.pmrv.api.workflow.request.flow.installation.common.handler;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRegulatorLedPeerReviewPreviewPermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRegulatorLedPreviewPermitDocumentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitPreviewHandlerTest {


    private static final long taskId = 1L;
    private static final DecisionNotification decisionNotification = DecisionNotification.builder().build();
    private static final FileDTO fileDTO = FileDTO.builder().fileName("fileName").build();

    @InjectMocks
    private PermitPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Spy
    private ArrayList<PermitPreviewDocumentService> previewDocumentServices;



    private static Stream<Arguments> provideGenerateDocumentTestArguments() {

        List<Arguments> arguments = List.of(
                Arguments.of(mock(PermitVariationRegulatorLedPreviewPermitDocumentService.class),RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT),
                Arguments.of(mock(PermitVariationRegulatorLedPeerReviewPreviewPermitDocumentService.class),RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW)
                );

        for(Arguments arg: arguments){
            PermitPreviewDocumentService service = ((PermitPreviewDocumentService) Arrays.stream(arg.get()).toList().get(0));
            RequestTaskType requestTaskType = ((RequestTaskType) Arrays.stream(arg.get()).toList().get(1));
            when(service.getTypes()).thenReturn(List.of(requestTaskType));
            when(service.create(taskId, decisionNotification)).thenReturn(fileDTO);
        }

        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("provideGenerateDocumentTestArguments")
    void generateDocument(PermitPreviewDocumentService service, RequestTaskType requestTaskType) {
        this.previewDocumentServices.add(service);

        when(requestTaskService.findTaskById(taskId)).thenReturn(RequestTask.builder().type(
            requestTaskType).build()
        );

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(service, times(1)).create(taskId, decisionNotification);
    }

    @ParameterizedTest
    @MethodSource("provideGenerateDocumentTestArguments")
    void generateDocument_previewDocumentServiceDoesntExist_throwException(PermitPreviewDocumentService service) {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.previewDocumentServices.add(service);
            when(requestTaskService.findTaskById(taskId)).thenReturn(RequestTask.builder().type(RequestTaskType.PERMIT_VARIATION_APPLICATION_SUBMIT).build());
            handler.generateDocument(taskId, decisionNotification);

        });

        String actualCode = exception.getErrorCode().getCode();
        assertEquals(actualCode, MetsErrorCode.INVALID_DOCUMENT_TEMPLATE_FOR_REQUEST_TASK.getCode());
        assertTrue(Arrays.asList(exception.getData()).contains(RequestTaskType.PERMIT_VARIATION_APPLICATION_SUBMIT));
    }

}
