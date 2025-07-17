package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification.PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermitTransferAcceptedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitTransferAcceptedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider commonProvider;

    @Test
    void generateDocument() {
        final Long taskId = 2L;
        final long accountId = 3L;
        final String relatedRequestId = "1";
        final String filename = "permit_transfer_notice.pdf";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitTransferBRequestPayload requestPayload = PermitTransferBRequestPayload.builder().relatedRequestId(relatedRequestId).build();
        final Request request = Request.builder().accountId(accountId)
                .payload(requestPayload)
                .build();
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload =
                PermitTransferBApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final TemplateParams templateParams = TemplateParams.builder().build();
        final Map<String, Object> params = Map.of();
        final FileDTO fileDTO = FileDTO.builder().fileName(filename).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request,decisionNotification)).thenReturn(templateParams);

        final String transfererRequestId = ((PermitTransferBRequestPayload) request.getPayload()).getRelatedRequestId();

        when(requestService.findRequestById(transfererRequestId)).thenReturn(request);
        when(commonProvider.constructParams(request, request)).thenReturn(params);

        templateParams.getParams().putAll(params);

        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_TRANSFER_ACCEPTED,
                templateParams,
                filename)
        ).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(requestService, times(1)).findRequestById(transfererRequestId);
        verify(commonProvider, times(1)).constructParams(request, request);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.PERMIT_TRANSFER_ACCEPTED,
                templateParams,
                filename);


    }
}
