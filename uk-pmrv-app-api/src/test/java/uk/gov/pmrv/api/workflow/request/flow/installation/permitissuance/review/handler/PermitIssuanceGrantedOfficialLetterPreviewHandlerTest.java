package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceGrantedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitIssuanceGrantedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void previewDocument_GHGE() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder().permitType(PermitType.GHGE).build();
        final Request request = Request.builder().payload(requestPayload).accountId(accountId).build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED,
            templateParams,
            "GHGE_permit_application_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED,
            templateParams,
            "GHGE_permit_application_approved.pdf");
    }

    @Test
    void previewDocument_HSE() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder().permitType(PermitType.HSE).build();
        final Request request = Request.builder().payload(requestPayload).accountId(accountId).build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED,
            templateParams,
            "HSE_permit_application_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED,
            templateParams,
            "HSE_permit_application_approved.pdf");
    }

    @Test
    void previewDocument_WASTE() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder().permitType(PermitType.WASTE).build();
        final Request request = Request.builder().payload(requestPayload).accountId(accountId).build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED,
            templateParams,
            "WASTE_permit_application_approved.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED,
            templateParams,
            "WASTE_permit_application_approved.pdf");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(DocumentTemplateType.PERMIT_ISSUANCE_GHGE_ACCEPTED,
                DocumentTemplateType.PERMIT_ISSUANCE_HSE_ACCEPTED,
                DocumentTemplateType.PERMIT_ISSUANCE_WASTE_ACCEPTED);
    }

    @Test
    void getTaskTypes() {
        assertThat(handler.getTaskTypes()).containsExactly(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW,
                RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW);
    }
}
