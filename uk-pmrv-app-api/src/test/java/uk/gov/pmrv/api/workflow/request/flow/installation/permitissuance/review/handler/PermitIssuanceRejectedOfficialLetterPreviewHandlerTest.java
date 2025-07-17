package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRejectedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitIssuanceRejectedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Test
    void initializePayload() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final Request request = Request.builder().accountId(accountId).build();
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
                .determination(PermitIssuanceRejectDetermination.builder()
                    .type(DeterminationType.GRANTED)
                    .reason("Reason")
                    .officialNotice("officialRefusalLetter")
                    .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(taskPayload)
            .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName("filename").build();
        final Map<String, Object> variationParams = Map.of(
            "officialRefusalLetter", "officialRefusalLetter"
        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder().params(variationParams).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(documentFileGeneratorService.generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_REJECTED,
            templateParamsWithCustom,
            "permit_application_rejection_notice.pdf")).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
            DocumentTemplateType.PERMIT_ISSUANCE_REJECTED,
            templateParamsWithCustom,
            "permit_application_rejection_notice.pdf");
        assertThat(templateParams.getParams()).containsExactlyInAnyOrderEntriesOf(variationParams);
    }
}
