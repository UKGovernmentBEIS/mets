package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

@Service
@RequiredArgsConstructor
public class BDRS2VerificationSubmitService {

    private final BDRS2ValidationService bdrs2ValidationService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestService requestService;
    private final BDRS2Mapper bdrs2Mapper;

    @Transactional
    public void applySaveAction(
            BDRS2ApplicationVerificationSaveRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        BDRS2RequestPayload requestPayload = ((BDRS2RequestPayload) request.getPayload());

        BDRS2ApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.getVerificationReport().setVerificationData(taskActionPayload.getVerificationData());
        taskPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload
                .setVerificationSectionsCompleted(
                        taskActionPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());
    }

    @Transactional
    public void sendToOperator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();
        BDRS2ApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        bdrs2ValidationService.validateVerificationReport(taskPayload.getVerificationReport());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.setVerificationPerformed(true);
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload.setVerificationSectionsCompleted(taskPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());

        InstallationOperatorDetails installationOperatorDetails =
                installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());

        BDRS2ApplicationVerificationSubmittedRequestActionPayload actionPayload =
                bdrs2Mapper.toBDRS2ApplicationVerificationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setBdrs2Attachments(taskPayload.getBdrs2Attachments());

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.BDRS2_APPLICATION_VERIFICATION_SUBMITTED,
                appUser.getUserId());
    }
}
