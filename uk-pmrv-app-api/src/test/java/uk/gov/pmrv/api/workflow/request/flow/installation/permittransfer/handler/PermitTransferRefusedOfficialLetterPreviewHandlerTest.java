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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermitTransferRefusedOfficialLetterPreviewHandlerTest {
    @InjectMocks
    private PermitTransferRefusedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void generateDocument() {
        final Long taskId = 2L;
        final long accountId = 3L;
        final String filename = "permit_transfer_refused_notice.pdf";
        final String reason = "reason";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitTransferBRequestPayload requestPayload = PermitTransferBRequestPayload.builder().build();
        final Request request = Request.builder().accountId(accountId)
                .payload(requestPayload)
                .build();
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload =
                PermitTransferBApplicationReviewRequestTaskPayload.builder()
                        .permitTransferDetails(
                                PermitTransferDetails.builder().reason(reason).build())
                        .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();

        final TemplateParams templateParams = TemplateParams.builder().build();
        final Map<String, Object> extraParams = Map.of(
                "officialNotice", taskPayload.getPermitTransferDetails().getReason());

        templateParams.getParams().putAll(extraParams);

        final FileDTO fileDTO = FileDTO.builder().fileName(filename).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request,decisionNotification)).thenReturn(templateParams);

        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_TRANSFER_REFUSED,
                templateParams,
                filename)
        ).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.PERMIT_TRANSFER_REFUSED,
                templateParams,
                filename);
    }

}
