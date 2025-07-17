package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRValidationService;


@Service
@RequiredArgsConstructor
public class ALRVerificationSubmitService {

    private final ALRValidationService alrValidationService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);
    private final RequestService requestService;

    @Transactional
    public void applySaveAction(
            ALRApplicationVerificationSaveRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = ((ALRRequestPayload) request.getPayload());

        ALRApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (ALRApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload
                .setVerificationSectionsCompleted(
                        taskActionPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());
    }

    public void sendToOperator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        ALRApplicationVerificationSubmitRequestTaskPayload taskPayload = (ALRApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        alrValidationService.validateVerificationReport(taskPayload.getVerificationReport());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.setVerificationPerformed(true);
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload.setVerificationSectionsCompleted(taskPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());
        ALRApplicationVerificationSubmittedRequestActionPayload actionPayload =
                ALR_MAPPER.toALRApplicationVerificationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setAlrAttachments(taskPayload.getAlrAttachments());

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.ALR_APPLICATION_VERIFICATION_SUBMITTED,
                appUser.getUserId());
    }
}
