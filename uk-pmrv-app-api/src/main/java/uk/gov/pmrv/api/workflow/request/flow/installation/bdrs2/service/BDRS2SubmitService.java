package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2ValidationService;

@Service
@RequiredArgsConstructor
public class BDRS2SubmitService {

    private final RequestService requestService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final BDRS2ValidationService bdrs2ValidationService;
    private static final BDRS2Mapper BDRS2_MAPPER = Mappers.getMapper(BDRS2Mapper.class);

    public void applySaveAction(RequestTask requestTask,
                                BDRS2ApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setBdrs2SectionsCompleted(
                taskActionPayload.getBdrs2SectionsCompleted());
        taskPayload.setBdrs2(taskActionPayload.getBdrs2());
        taskPayload.setBdrs2FileVersion(taskActionPayload.getBdrs2FileVersion());
    }

    public void submitToRegulator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();
        BDRS2ApplicationSubmitRequestTaskPayload taskPayload = (BDRS2ApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Delete verificationReport if verificationPerformed = false
        if (!taskPayload.isVerificationPerformed()) {
            requestPayload.setVerificationReport(null);
        }

        // Validate verification report if verification was performed
        if (!ObjectUtils.isEmpty(requestPayload.getVerificationReport())) {
            bdrs2ValidationService.validateVerificationReport(requestPayload.getVerificationReport());
        }

        // Validate BDRS2 data
        bdrs2ValidationService.validateBDRS2(taskPayload.getBdrs2());

        // Validate BDRS2 file name
        if (taskPayload.getBdrs2().getBdrs2Files() != null) {
            String fileName = taskPayload.getBdrs2Attachments().get(taskPayload.getBdrs2().getBdrs2Files().getFile());
            bdrs2ValidationService.validateBDRS2FileName(fileName);
        }

        // Create request action payload
        BDRS2ApplicationSubmittedRequestActionPayload actionPayload =
                createApplicationSubmittedRequestActionPayload(
                        requestTask,
                        taskPayload,
                        requestPayload,
                        RequestActionPayloadType.BDRS2_APPLICATION_SUBMITTED_PAYLOAD);

        // Save BDRS2 data to request payload
        requestPayload.setBdrs2(taskPayload.getBdrs2());
        requestPayload.setBdrs2Attachments(taskPayload.getBdrs2Attachments());
        requestPayload.setBdrs2SectionsCompleted(taskPayload.getBdrs2SectionsCompleted());
        requestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());

        // Add request action
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.BDRS2_APPLICATION_SENT_TO_REGULATOR,
                appUser.getUserId());
    }

    public void submitToVerifier(BDRS2ApplicationSubmitToVerifierRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();
        BDRS2ApplicationSubmitRequestTaskPayload taskPayload = (BDRS2ApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate BDRS2 data
        bdrs2ValidationService.validateBDRS2(taskPayload.getBdrs2());

        // Validate BDRS2 file name
        if (taskPayload.getBdrs2().getBdrs2Files() != null) {
            String fileName = taskPayload.getBdrs2Attachments().get(taskPayload.getBdrs2().getBdrs2Files().getFile());
            bdrs2ValidationService.validateBDRS2FileName(fileName);
        }

        // Create request action payload
        BDRS2ApplicationSubmittedRequestActionPayload requestActionPayload =
                createApplicationSubmittedRequestActionPayload(
                        requestTask,
                        taskPayload,
                        requestPayload,
                        RequestActionPayloadType.BDRS2_APPLICATION_SUBMITTED_PAYLOAD);

        // Save BDRS2 data to request payload
        requestPayload.setBdrs2(taskPayload.getBdrs2());
        requestPayload.setBdrs2Attachments(taskPayload.getBdrs2Attachments());
        requestPayload.setBdrs2SectionsCompleted(taskPayload.getBdrs2SectionsCompleted());
        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        // Add request action
        requestService.addActionToRequest(
                request,
                requestActionPayload,
                RequestActionType.BDRS2_APPLICATION_SENT_TO_VERIFIER,
                appUser.getUserId());
    }

    private BDRS2ApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(
            RequestTask requestTask,
            BDRS2ApplicationSubmitRequestTaskPayload taskPayload,
            BDRS2RequestPayload requestPayload,
            RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails =
                installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        BDRS2ApplicationSubmittedRequestActionPayload actionPayload =
                BDRS2_MAPPER.toBDRS2ApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setBdrs2Attachments(taskPayload.getBdrs2Attachments());

        if (taskPayload.isVerificationPerformed()) {
            actionPayload.setVerificationReport(requestPayload.getVerificationReport());
            actionPayload.setVerificationAttachments(requestPayload.getVerificationAttachments());
        }

        return actionPayload;
    }
}
