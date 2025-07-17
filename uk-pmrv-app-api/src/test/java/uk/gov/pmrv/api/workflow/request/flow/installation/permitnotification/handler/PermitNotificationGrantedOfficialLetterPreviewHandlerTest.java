package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationCommonDocumentTemplateWorkflowParamsProvider;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGrantedOfficialLetterPreviewHandlerTest {

    @InjectMocks
    private PermitNotificationGrantedOfficialLetterPreviewHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private InstallationPreviewOfficialNoticeService previewOfficialNoticeService;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;

    @Mock
    private PermitNotificationCommonDocumentTemplateWorkflowParamsProvider permitNotificationCommonParamsProvider;

    @Test
    void generateDocument() {

        final Long taskId = 2L;
        final long accountId = 3L;
        final String filename = "Permit Notification Acknowledgement Letter.pdf";
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder().build();
        final Request request = Request.builder().accountId(accountId)
                .payload(requestPayload)
                .build();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                PermitNotificationApplicationReviewRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewDecision(PermitNotificationReviewDecision.builder()
                                .type(PermitNotificationReviewDecisionType.ACCEPTED)
                                .details(PermitNotificationAcceptedDecisionDetails.builder()
                                        .officialNotice("officialNotice")
                                        .build())
                                .build())
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();
        final TemplateParams templateParams = TemplateParams.builder().build();
        final FileDTO fileDTO = FileDTO.builder().fileName(filename).build();
        final Map<String, Object> variationParams = Map.of(
                "officialNotice", "officialNotice",
                "cessationDate", "test",
                "resumptionDate", "test",
                "followUpRequired", false,

                "isPermanentCessation", taskPayload.getReviewDecision().getType().equals(PermitNotificationReviewDecisionType.PERMANENT_CESSATION),
                "isTemporaryCessation", taskPayload.getReviewDecision().getType().equals(PermitNotificationReviewDecisionType.TEMPORARY_CESSATION),
                "isTreatedAsPermanentCessation", taskPayload.getReviewDecision().getType().equals(PermitNotificationReviewDecisionType.CESSATION_TREATED_AS_PERMANENT),
                "isNotCessation", taskPayload.getReviewDecision().getType().equals(PermitNotificationReviewDecisionType.NOT_CESSATION)

        );
        final TemplateParams templateParamsWithCustom = TemplateParams.builder().params(variationParams).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(previewOfficialNoticeService.generateCommonParams(request, decisionNotification)).thenReturn(templateParams);
        when(permitNotificationCommonParamsProvider.constructParams(taskPayload)).thenReturn(variationParams);
        when(documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED,
                templateParamsWithCustom,
                filename)
        ).thenReturn(fileDTO);

        final FileDTO result = handler.generateDocument(taskId, decisionNotification);

        assertEquals(result, fileDTO);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(previewOfficialNoticeService, times(1)).generateCommonParams(request, decisionNotification);
        verify(permitNotificationCommonParamsProvider, times(1)).constructParams(taskPayload);
        verify(documentFileGeneratorService, times(1)).generateFileDocument(
                DocumentTemplateType.PERMIT_NOTIFICATION_ACCEPTED,
                templateParamsWithCustom,
                filename);
    }
}
