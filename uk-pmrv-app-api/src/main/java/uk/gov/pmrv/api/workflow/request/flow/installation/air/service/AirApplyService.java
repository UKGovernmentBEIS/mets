package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.mapper.AirMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.validation.AirSubmitValidator;

@Service
@RequiredArgsConstructor
public class AirApplyService {

    private final RequestService requestService;
    private final AirSubmitValidator submitValidatorService;
    private static final AirMapper AIR_MAPPER = Mappers.getMapper(AirMapper.class);

    @Transactional
    public void applySaveAction(final AirSaveApplicationRequestTaskActionPayload taskActionPayload,
                                final RequestTask requestTask) {
        
        final AirApplicationSubmitRequestTaskPayload taskPayload = (AirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setOperatorImprovementResponses(taskActionPayload.getOperatorImprovementResponses());
        taskPayload.setAirSectionsCompleted(taskActionPayload.getAirSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final RequestTask requestTask, 
                                  final PmrvUser pmrvUser) {
        
        final AirApplicationSubmitRequestTaskPayload taskPayload = (AirApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate AIR
        submitValidatorService.validate(taskPayload.getOperatorImprovementResponses(), taskPayload.getAirImprovements());

        // Submit AIR
        this.submitAir(requestTask, pmrvUser);
    }

    private void submitAir(final RequestTask requestTask,
                           final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final AirRequestPayload airRequestPayload = (AirRequestPayload) request.getPayload();
        final AirApplicationSubmitRequestTaskPayload taskPayload = (AirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final AirRequestMetadata airRequestMetadata = (AirRequestMetadata) request.getMetadata();

        // Update request
        airRequestPayload.setOperatorImprovementResponses(taskPayload.getOperatorImprovementResponses());
        airRequestPayload.setAirAttachments(taskPayload.getAirAttachments());
        airRequestPayload.setAirSectionsCompleted(taskPayload.getAirSectionsCompleted());

        // Add submitted action
        final AirApplicationSubmittedRequestActionPayload actionPayload = AIR_MAPPER
            .toAirApplicationSubmittedRequestActionPayload(taskPayload, airRequestMetadata.getYear());

        requestService.addActionToRequest(
            requestTask.getRequest(),
            actionPayload,
            RequestActionType.AIR_APPLICATION_SUBMITTED,
            pmrvUser.getUserId());
    }
}
