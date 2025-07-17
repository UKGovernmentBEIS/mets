package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.OperatorAirImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper.AirMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.validation.AirRespondToRegulatorCommentsValidator;

@Service
@RequiredArgsConstructor
public class AirRespondToRegulatorCommentsService {

    private final AirRespondToRegulatorCommentsValidator validator;
    private final RequestService requestService;
    private final AirRespondToRegulatorCommentsNotificationService respondToRegulatorCommentsNotificationService;
    private static final AirMapper AIR_MAPPER = Mappers.getMapper(AirMapper.class);

    @Transactional
    public void applySaveAction(final AirSaveRespondToRegulatorCommentsRequestTaskActionPayload payload,
                                final RequestTask requestTask) {
        
        final AirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (AirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        
        // do not allow save on items that have been responded
        if (taskPayload.getRespondedItems().contains(payload.getReference())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // Validate reference
        validator.validateReferenceOnRegulator(payload.getReference(), taskPayload.getRegulatorImprovementResponses());

        // Update
        taskPayload.getOperatorImprovementFollowUpResponses().put(payload.getReference(), payload.getOperatorImprovementFollowUpResponse());
        taskPayload.setAirRespondToRegulatorCommentsSectionsCompleted(payload.getAirRespondToRegulatorCommentsSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload payload,
                                  final RequestTask requestTask,
                                  final AppUser appUser) {

        final Request request = requestTask.getRequest();
        final AirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (AirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();
        final AirRequestMetadata airRequestMetadata = (AirRequestMetadata) request.getMetadata();
        final Integer reference = payload.getReference();

        // do not allow submit on items that have been responded
        if (taskPayload.getRespondedItems().contains(payload.getReference())) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        taskPayload.setAirRespondToRegulatorCommentsSectionsCompleted(payload.getAirRespondToRegulatorCommentsSectionsCompleted());

        // Validate reference
        validator.validate(
            reference,
            taskPayload.getOperatorImprovementFollowUpResponses(), 
            taskPayload.getRegulatorImprovementResponses()
        );

        final OperatorAirImprovementFollowUpResponse followUpResponse = 
            taskPayload.getOperatorImprovementFollowUpResponses().get(reference);
        
        // Add Request Action
        final AirApplicationRespondedToRegulatorCommentsRequestActionPayload actionPayload =
                AIR_MAPPER.toAirApplicationRespondedToRegulatorCommentsRequestActionPayload(
                    taskPayload, 
                    airRequestMetadata.getYear(), 
                    followUpResponse,
                    reference);

        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS,
                appUser.getUserId());

        // Remove reference
        taskPayload.getRespondedItems().add(reference);

        // Send notification email to Regulator
        respondToRegulatorCommentsNotificationService.sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(request);
    }
}
