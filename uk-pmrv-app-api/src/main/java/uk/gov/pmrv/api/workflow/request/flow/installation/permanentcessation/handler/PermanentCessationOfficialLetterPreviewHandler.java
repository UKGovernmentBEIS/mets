package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.PreviewDocumentAbstractHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.InstallationPreviewOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationScope;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class PermanentCessationOfficialLetterPreviewHandler extends PreviewDocumentAbstractHandler {

    private static final String FILE_NAME = "Permanent_cessation_notice_preview.pdf";

    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final InstallationPreviewOfficialNoticeService installationPreviewOfficialNoticeService;


    public PermanentCessationOfficialLetterPreviewHandler(RequestTaskService requestTaskService,
                                                          final InstallationPreviewOfficialNoticeService previewOfficialNoticeService,
                                                          final DocumentFileGeneratorService documentFileGeneratorService) {
        super(requestTaskService);
        this.installationPreviewOfficialNoticeService = previewOfficialNoticeService;
        this.documentFileGeneratorService = documentFileGeneratorService;
    }

    @Override
    protected List<RequestTaskType> getTaskTypes() {
        return List.of(
                RequestTaskType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW,
                RequestTaskType.PERMANENT_CESSATION_APPLICATION_SUBMIT);
    }

    protected FileDTO generateDocument(Long taskId, DecisionNotification decisionNotification) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final Request request = requestTask.getRequest();

        final TemplateParams templateParams = installationPreviewOfficialNoticeService.generateCommonParamsWithExtraAccountDetails(request, decisionNotification);
        templateParams.getParams().put("additionalInformation", getAdditionDetails(requestTask));
        PermanentCessationApplicationSubmitRequestTaskPayload payload = (PermanentCessationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        templateParams.getParams().put("isWholeInstallation",
                payload.getPermanentCessation().getCessationScope()
                        .equals(PermanentCessationScope.WHOLE_INSTALLATION));
        templateParams.getParams().put("cessationDate",
                payload.getPermanentCessation().getCessationDate().format(
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ROOT)
                ));

        return documentFileGeneratorService.generateFileDocument(
                DocumentTemplateType.PERMANENT_CESSATION, //refers to the db key for the document template
                templateParams,
                FILE_NAME);
    }

    @Override
    public List<DocumentTemplateType> getTypes() {
        return List.of(
                DocumentTemplateType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW,
                DocumentTemplateType.PERMANENT_CESSATION_APPLICATION_SUBMIT);
    }

    private String getAdditionDetails(RequestTask requestTask) {
        return Optional.ofNullable(requestTask)
                .map(task -> (PermanentCessationApplicationSubmitRequestTaskPayload) task.getPayload())
                .map(PermanentCessationApplicationSubmitRequestTaskPayload::getPermanentCessation)
                .map(PermanentCessation::getAdditionalDetails)
                .orElse("");
    }
}
