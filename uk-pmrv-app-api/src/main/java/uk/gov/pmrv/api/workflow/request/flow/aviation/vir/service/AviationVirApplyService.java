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
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper.AviationVirMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation.AviationVirSubmitValidator;

@Service
@RequiredArgsConstructor
public class AviationVirApplyService {

    private final RequestService requestService;
    private final AviationVirSubmitValidator submitValidator;
    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Transactional
    public void applySaveAction(final AviationVirSaveApplicationRequestTaskActionPayload taskActionPayload,
                                final RequestTask requestTask) {

        final AviationVirApplicationSubmitRequestTaskPayload taskPayload =
            (AviationVirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setOperatorImprovementResponses(taskActionPayload.getOperatorImprovementResponses());
        taskPayload.setVirSectionsCompleted(taskActionPayload.getVirSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final RequestTask requestTask, 
                                  final PmrvUser pmrvUser) {
        
        final AviationVirApplicationSubmitRequestTaskPayload taskPayload = 
            (AviationVirApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate VIR
        submitValidator.validate(taskPayload.getOperatorImprovementResponses(), taskPayload.getVerificationData());

        // Submit VIR
        submitVir(requestTask, pmrvUser);
    }

    private void submitVir(final RequestTask requestTask, 
                           final PmrvUser pmrvUser) {
        
        final Request request = requestTask.getRequest();
        final AviationVirRequestPayload virRequestPayload = (AviationVirRequestPayload) request.getPayload();
        final AviationVirApplicationSubmitRequestTaskPayload
            taskPayload = (AviationVirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final AviationVirRequestMetadata virRequestMetadata = (AviationVirRequestMetadata) request.getMetadata();

        // Update request
        virRequestPayload.setVirAttachments(taskPayload.getVirAttachments());
        virRequestPayload.setVirSectionsCompleted(taskPayload.getVirSectionsCompleted());
        virRequestPayload.setOperatorImprovementResponses(taskPayload.getOperatorImprovementResponses());

        // Add submitted action
        final AviationVirApplicationSubmittedRequestActionPayload actionPayload = VIR_MAPPER
            .toVirApplicationSubmittedRequestActionPayload(taskPayload, virRequestMetadata.getYear());

        requestService.addActionToRequest(
            requestTask.getRequest(),
            actionPayload,
            RequestActionType.AVIATION_VIR_APPLICATION_SUBMITTED,
            pmrvUser.getUserId());
    }
}
