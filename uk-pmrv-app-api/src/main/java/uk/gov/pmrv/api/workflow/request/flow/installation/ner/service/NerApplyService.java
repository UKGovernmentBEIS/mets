package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NerApplyService {

    private final RequestService requestService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final NERValidationService nerValidationService;
    private static final NERMapper NER_MAPPER = Mappers.getMapper(NERMapper.class);

    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final NerSaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NerApplicationSubmitRequestTaskPayload
            requestTaskPayload = (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
        requestTaskPayload.setNer(taskActionPayload.getNer());
        requestTaskPayload.setNerFileVersion(taskActionPayload.getNerFileVersion());
    }

    public void submitToVerifier(NERApplicationSubmitToVerifierRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        NerApplicationSubmitRequestTaskPayload taskPayload = (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate NER data
        nerValidationService.validateNer(taskPayload.getNer());

        // Validate NER file name
        if (taskPayload.getNer().getNerFiles() != null) {
            String fileName = taskPayload.getNerAttachments().get(taskPayload.getNer().getNerFiles().getFile());
            nerValidationService.validateNerFileName(fileName);
        }

        incrementNerFileVersion(taskPayload, requestPayload);

        // Create request action payload
        NERApplicationSubmittedRequestActionPayload requestActionPayload =
                createApplicationSubmittedRequestActionPayload(
                        requestTask,
                        taskPayload,
                        requestPayload,
                        RequestActionPayloadType.NER_APPLICATION_SUBMITTED_PAYLOAD);

        // Save NER data to request payload
        requestPayload.setNer(taskPayload.getNer());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setNerSectionsCompleted(taskPayload.getNerSectionsCompleted());
//        requestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        // Add request action
        requestService.addActionToRequest(
                request,
                requestActionPayload,
                RequestActionType.NER_APPLICATION_SENT_TO_VERIFIER,
                appUser.getUserId());
    }

    public NERApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(
            RequestTask requestTask,
            NerApplicationSubmitRequestTaskPayload taskPayload,
            NerRequestPayload requestPayload,
            RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails =
                installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        NERApplicationSubmittedRequestActionPayload actionPayload =
                NER_MAPPER.toNERApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setNerAttachments(taskPayload.getNerAttachments());

//        if (taskPayload.isVerificationPerformed()) {
//            actionPayload.setVerificationReport(requestPayload.getVerificationReport());
//            actionPayload.setVerificationAttachments(requestPayload.getVerificationAttachments());
//        }

        return actionPayload;
    }

    private void incrementNerFileVersion(NerApplicationSubmitRequestTaskPayload taskPayload, NerRequestPayload requestPayload) {
        UUID requestFile = Optional.ofNullable(requestPayload.getNer().getNerFiles())
                .map(NERFiles::getFile)
                .orElse(null);

        UUID taskFile = Optional.ofNullable(taskPayload.getNer().getNerFiles())
                .map(NERFiles::getFile)
                .orElse(null);

        if (!Objects.equals(requestFile, taskFile)) {
            requestPayload.incrementNerFileVersion();
        }
    }
}
