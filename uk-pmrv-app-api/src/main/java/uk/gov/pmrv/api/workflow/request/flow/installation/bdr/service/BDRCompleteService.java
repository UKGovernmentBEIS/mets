package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

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
import uk.gov.pmrv.api.reporting.service.bdr.BaselineDataReportFreeAllocationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper.BDRMapper;

@Service
@RequiredArgsConstructor
public class BDRCompleteService {

    private final RequestService requestService;
    private static final BDRMapper BDR_MAPPER = Mappers.getMapper(BDRMapper.class);
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestVerificationService requestVerificationService;
    private final AccountFileAttachmentService accountFileAttachmentService;
    private final BaselineDataReportFreeAllocationService baselineDataReportFreeAllocationService;

    @Transactional
    public void complete(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        addFreeAllocationEntry(request);
        saveAccountFileAttachment(requestId);
    }

    private void addFreeAllocationEntry(Request request) {
        final BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();
        Boolean freeAllocation = requestPayload.getBdr().getIsApplicationForFreeAllocation();
        baselineDataReportFreeAllocationService.createFreeAllocationEntry(request.getAccountId(), freeAllocation);
    }

    public void addRequestAction(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        final BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();

        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

 		requestVerificationService.refreshVerificationReportVBDetails(requestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        final BDRApplicationCompletedRequestActionPayload actionPayload = 
                BDR_MAPPER.toBDRApplicationCompletedRequestActionPayload(requestPayload, installationOperatorDetails, requestPayload.getVerificationReport());

        actionPayload.setBdrAttachments(requestPayload.getBdrAttachments());
        actionPayload.setRegulatorReviewAttachments(requestPayload.getRegulatorReviewAttachments());

        RequestActionType actionType  = RequestActionType.BDR_APPLICATION_COMPLETED;

        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }

    private void saveAccountFileAttachment(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        BDRRequestPayload requestPayload = (BDRRequestPayload) request.getPayload();

        if (requestPayload.getRegulatorReviewOutcome().getBdrFile() != null) {
            accountFileAttachmentService.updateOrInsertAccountFileAttachment(AccountFileAttachmentDTO.builder()
                .workflow(AccountFileAttachmentWorkflow.BDR)
                .workflowSubtype(AccountFileAttachmentWorkflowSubType.BDR_ATTACHMENT)
                .originatedRequestId(requestId)
                .status(AccountFileAttachmentStatus.FINALIZED)
                .accountId(request.getAccountId())
                .period("2026-2030")
                .fileUuid(requestPayload.getRegulatorReviewOutcome().getBdrFile().toString())
                .competentAuthority(request.getCompetentAuthority())
                .build());
        }
    }
}
