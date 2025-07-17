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
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper.VirMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation.VirSubmitValidatorService;

@Service
@RequiredArgsConstructor
public class RequestVirApplyService {

    private final RequestService requestService;
    private final VirSubmitValidatorService virSubmitValidatorService;
    private static final VirMapper virMapper = Mappers.getMapper(VirMapper.class);

    @Transactional
    public void applySaveAction(VirSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        VirApplicationSubmitRequestTaskPayload taskPayload = (VirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setOperatorImprovementResponses(taskActionPayload.getOperatorImprovementResponses());
        taskPayload.setVirSectionsCompleted(taskActionPayload.getVirSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(RequestTask requestTask, AppUser appUser) {
        final VirApplicationSubmitRequestTaskPayload taskPayload = (VirApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate VIR
        virSubmitValidatorService.validate(taskPayload.getOperatorImprovementResponses(), taskPayload.getVerificationData());

        // Submit VIR
        submitVir(requestTask, appUser);
    }

    private void submitVir(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        VirRequestPayload virRequestPayload = (VirRequestPayload) request.getPayload();
        final VirApplicationSubmitRequestTaskPayload taskPayload = (VirApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final VirRequestMetadata virRequestMetadata = (VirRequestMetadata) request.getMetadata();

        // Update request
        virRequestPayload.setVirAttachments(taskPayload.getVirAttachments());
        virRequestPayload.setVirSectionsCompleted(taskPayload.getVirSectionsCompleted());
        virRequestPayload.setOperatorImprovementResponses(taskPayload.getOperatorImprovementResponses());

        // Add submitted action
        VirApplicationSubmittedRequestActionPayload actionPayload = virMapper
                .toVirApplicationSubmittedRequestActionPayload(taskPayload, virRequestMetadata.getYear());

        requestService.addActionToRequest(
                requestTask.getRequest(),
                actionPayload,
                RequestActionType.VIR_APPLICATION_SUBMITTED,
                appUser.getUserId());
    }
}
