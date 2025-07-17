package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation.BDRValidationService;

@Service
@RequiredArgsConstructor
public class BDRVerificationSubmitService {

    private final BDRValidationService bdrValidationService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);
    private final RequestService requestService;

    @Transactional
    public void applySaveAction(
            BDRApplicationVerificationSaveRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        BDRRequestPayload requestPayload = ((BDRRequestPayload) request.getPayload());

        BDRApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (BDRApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

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
        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();
        BDRApplicationVerificationSubmitRequestTaskPayload taskPayload = (BDRApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        bdrValidationService.validateVerificationReport(taskPayload.getVerificationReport());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.setVerificationPerformed(true);
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload.setVerificationSectionsCompleted(taskPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());
        BDRApplicationVerificationSubmittedRequestActionPayload actionPayload =
                BDR_MAPPER.toBDRApplicationVerificationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setBdrAttachments(taskPayload.getBdrAttachments());

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.BDR_APPLICATION_VERIFICATION_SUBMITTED,
                appUser.getUserId());
    }
}
