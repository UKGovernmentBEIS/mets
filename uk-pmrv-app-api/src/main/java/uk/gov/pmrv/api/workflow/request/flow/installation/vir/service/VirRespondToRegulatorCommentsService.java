package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirRespondToRegulatorCommentsNotificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirRespondToRegulatorCommentsValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper.VirMapper;

@Service
@RequiredArgsConstructor
public class VirRespondToRegulatorCommentsService {

    private final VirRespondToRegulatorCommentsValidator virRespondToRegulatorCommentsValidator;
    private final RequestService requestService;
    private final VirRespondToRegulatorCommentsNotificationService virRespondToRegulatorCommentsNotificationService;
    private static final VirMapper virMapper = Mappers.getMapper(VirMapper.class);

    @Transactional
    public void applySaveAction(final VirSaveRespondToRegulatorCommentsRequestTaskActionPayload payload,
                                RequestTask requestTask) {
        final VirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (VirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();

        // Validate reference
        virRespondToRegulatorCommentsValidator.validateReferenceOnRegulator(payload.getReference(),
                taskPayload.getRegulatorImprovementResponses());

        // Update
        taskPayload.getOperatorImprovementFollowUpResponses().put(payload.getReference(), payload.getOperatorImprovementFollowUpResponse());
        taskPayload.setVirRespondToRegulatorCommentsSectionsCompleted(payload.getVirRespondToRegulatorCommentsSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload payload,
                                  RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        VirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (VirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        final VirRequestPayload virRequestPayload = (VirRequestPayload) request.getPayload();
        final VirRequestMetadata virRequestMetadata = (VirRequestMetadata) request.getMetadata();

        // Validate reference
        virRespondToRegulatorCommentsValidator.validate(payload.getReference(),
                taskPayload.getOperatorImprovementFollowUpResponses(), taskPayload.getRegulatorImprovementResponses());

        // Remove reference
        OperatorImprovementFollowUpResponse followUpResponse = taskPayload.getOperatorImprovementFollowUpResponses().remove(payload.getReference());
        taskPayload.getRegulatorImprovementResponses().remove(payload.getReference());
        taskPayload.setVirRespondToRegulatorCommentsSectionsCompleted(payload.getVirRespondToRegulatorCommentsSectionsCompleted());

        // Add Request Action
        VirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                virMapper.toVirApplicationRespondedToRegulatorCommentsRequestActionPayload(
                        virRequestPayload, virRequestMetadata.getYear(), followUpResponse, payload.getReference());

        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS,
                appUser.getUserId());

        // Send notification email to Regulator
        virRespondToRegulatorCommentsNotificationService
                .sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);
    }
}
