package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper.AviationVirMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirRespondToRegulatorCommentsNotificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirRespondToRegulatorCommentsValidator;

@Service
@RequiredArgsConstructor
public class AviationVirRespondToRegulatorCommentsService {

    private final VirRespondToRegulatorCommentsValidator virRespondToRegulatorCommentsValidator;
    private final RequestService requestService;
    private final VirRespondToRegulatorCommentsNotificationService virRespondToRegulatorCommentsNotificationService;
    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Transactional
    public void applySaveAction(final AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload payload,
                                final RequestTask requestTask) {
        
        final AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();

        // Validate reference
        virRespondToRegulatorCommentsValidator.validateReferenceOnRegulator(
            payload.getReference(),
            taskPayload.getRegulatorImprovementResponses()
        );

        // Update
        taskPayload.getOperatorImprovementFollowUpResponses().put(payload.getReference(), payload.getOperatorImprovementFollowUpResponse());
        taskPayload.setVirRespondToRegulatorCommentsSectionsCompleted(payload.getVirRespondToRegulatorCommentsSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload payload,
                                  final RequestTask requestTask,
                                  final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        final AviationVirRequestPayload virRequestPayload = (AviationVirRequestPayload) request.getPayload();
        final AviationVirRequestMetadata virRequestMetadata = (AviationVirRequestMetadata) request.getMetadata();

        // Validate reference
        virRespondToRegulatorCommentsValidator.validate(
            payload.getReference(),
            taskPayload.getOperatorImprovementFollowUpResponses(), 
            taskPayload.getRegulatorImprovementResponses()
        );

        // Remove reference
        final OperatorImprovementFollowUpResponse followUpResponse = 
            taskPayload.getOperatorImprovementFollowUpResponses().remove(payload.getReference());
        taskPayload.getRegulatorImprovementResponses().remove(payload.getReference());
        taskPayload.setVirRespondToRegulatorCommentsSectionsCompleted(payload.getVirRespondToRegulatorCommentsSectionsCompleted());

        // Add Request Action
        final AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                VIR_MAPPER.toVirApplicationRespondedToRegulatorCommentsRequestActionPayload(
                        virRequestPayload, virRequestMetadata.getYear(), followUpResponse, payload.getReference()
                );

        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS,
                pmrvUser.getUserId()
        );

        // Send notification email to Regulator
        virRespondToRegulatorCommentsNotificationService.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);
    }
}
