package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.mapper.BDRS2Mapper;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BDRS2CompleteService {

    private final RequestService requestService;
    private static final BDRS2Mapper BDRS2_MAPPER = Mappers.getMapper(BDRS2Mapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;
    private final AccountFileAttachmentService accountFileAttachmentService;

    @Transactional
    public void complete(final String requestId) {
        saveAccountFileAttachment(requestId);
    }

    public void addRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();


        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

        requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
                request.getVerificationBodyId());

        final BDRS2ApplicationCompletedRequestActionPayload actionPayload =
                BDRS2_MAPPER.toBDRS2ApplicationCompletedRequestActionPayload(requestPayload, installationOperatorDetails, requestPayload.getVerificationReport());

        actionPayload.setBdrs2Attachments(requestPayload.getBdrs2Attachments());
        actionPayload.setRegulatorReviewAttachments(requestPayload.getRegulatorReviewAttachments());

        RequestActionType actionType  = RequestActionType.BDRS2_APPLICATION_COMPLETED;

        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }

    private void saveAccountFileAttachment(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        BDRS2RequestPayload requestPayload = (BDRS2RequestPayload) request.getPayload();
        final String period = "2026-2030";

        UUID bdrs2File = requestPayload.getBdrs2().getBdrs2Files().getFile();

        if (bdrs2File != null) {
            accountFileAttachmentService.updateOrInsertAccountFileAttachment(AccountFileAttachmentDTO.builder()
                    .workflow(AccountFileAttachmentWorkflow.BDRS2)
                    .workflowSubtype(AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT)
                    .originatedRequestId(requestId)
                    .status(AccountFileAttachmentStatus.FINALIZED)
                    .accountId(request.getAccountId())
                    .period(period)
                    .fileUuid(bdrs2File.toString())
                    .competentAuthority(request.getCompetentAuthority())
                    .build());
        }

        if (requestPayload.getBdrs2().getMmpFiles() != null) {
            UUID mmpFile = requestPayload.getBdrs2().getMmpFiles().getFile();
            if (mmpFile != null) {
                accountFileAttachmentService.updateOrInsertAccountFileAttachment(AccountFileAttachmentDTO.builder()
                        .workflow(AccountFileAttachmentWorkflow.BDRS2)
                        .workflowSubtype(AccountFileAttachmentWorkflowSubType.BDRS2_MMP)
                        .originatedRequestId(requestId)
                        .status(AccountFileAttachmentStatus.FINALIZED)
                        .accountId(request.getAccountId())
                        .period(period)
                        .fileUuid(mmpFile.toString())
                        .competentAuthority(request.getCompetentAuthority())
                        .build());
            }
        }

        Optional.ofNullable(requestPayload.getVerificationReport())
                .map(BDRS2VerificationReport::getVerificationData)
                .map(BDRS2VerificationData::getOpinionStatement)
                .map(BDRS2VerificationOpinionStatement::getOpinionStatementFile)
                .ifPresent(vosFile -> accountFileAttachmentService.updateOrInsertAccountFileAttachment(
                        AccountFileAttachmentDTO.builder()
                                .workflow(AccountFileAttachmentWorkflow.BDRS2)
                                .workflowSubtype(AccountFileAttachmentWorkflowSubType.BDRS2_VOS)
                                .originatedRequestId(requestId)
                                .status(AccountFileAttachmentStatus.FINALIZED)
                                .accountId(request.getAccountId())
                                .period(period)
                                .fileUuid(vosFile.toString())
                                .competentAuthority(request.getCompetentAuthority())
                                .build()));
    }
}
