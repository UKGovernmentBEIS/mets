package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper.DoalMapper;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentStatus;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflow;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachmentWorkflowSubType;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentService;


@Service
@RequiredArgsConstructor
public class DoalCloseService {

    private final RequestService requestService;
    private final AccountFileAttachmentService accountFileAttachmentService;
    private static final DoalMapper DOAL_MAPPER = Mappers.getMapper(DoalMapper.class);

    @Transactional
    public void addClosedRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        if (requestPayload.getDoal() != null && requestPayload.getDoal().getOperatorActivityLevelReport() != null
                && requestPayload.getDoal().getOperatorActivityLevelReport().getDocument() != null) {
            updateOrInsertAccountFileAttachment(request, requestPayload, AccountFileAttachmentWorkflowSubType.ALR_ATTACHMENT,
                    requestPayload.getDoal().getOperatorActivityLevelReport().getDocument().toString());
        }

        DoalApplicationClosedRequestActionPayload actionPayload = DOAL_MAPPER
                .toDoalApplicationClosedRequestActionPayload(requestPayload);

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.DOAL_APPLICATION_CLOSED,
                requestPayload.getRegulatorAssignee());
    }

    private void updateOrInsertAccountFileAttachment(Request request, DoalRequestPayload requestPayload,
                                                     AccountFileAttachmentWorkflowSubType subType, String file) {
        AccountFileAttachmentDTO accountFileAttachmentDTO = AccountFileAttachmentDTO.builder()
                .workflow(AccountFileAttachmentWorkflow.DOAL)
                .workflowSubtype(subType)
                .originatedRequestId(request.getId())
                .status(AccountFileAttachmentStatus.FINALIZED)
                .accountId(request.getAccountId())
                .period(requestPayload.getReportingYear().toString())
                .fileUuid(file)
                .competentAuthority(request.getCompetentAuthority())
                .build();

        accountFileAttachmentService.updateOrInsertAccountFileAttachment(accountFileAttachmentDTO);
    }
}

