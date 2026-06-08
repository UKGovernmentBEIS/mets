package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

@Service
@RequiredArgsConstructor
public class NERVerificationSubmitService {

    private final NERValidationService nerValidationService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestService requestService;
    private final NERMapper nerMapper;

    @Transactional
    public void applySaveAction(
            NERApplicationVerificationSaveRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = ((NerRequestPayload) request.getPayload());

        NERApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (NERApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

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
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        NERApplicationVerificationSubmitRequestTaskPayload taskPayload =
                (NERApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        nerValidationService.validateVerificationReport(taskPayload.getVerificationReport());

        requestPayload.setVerificationReport(taskPayload.getVerificationReport());
        requestPayload.setVerificationPerformed(true);
        requestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        requestPayload.setVerificationSectionsCompleted(taskPayload.getVerificationSectionsCompleted());
        requestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());

        InstallationOperatorDetails installationOperatorDetails =
                installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());

        NERApplicationVerificationSubmittedRequestActionPayload actionPayload =
                nerMapper.toNERApplicationVerificationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setNerAttachments(taskPayload.getNerAttachments());

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.NER_APPLICATION_VERIFICATION_SUBMITTED,
                appUser.getUserId());
    }
}
