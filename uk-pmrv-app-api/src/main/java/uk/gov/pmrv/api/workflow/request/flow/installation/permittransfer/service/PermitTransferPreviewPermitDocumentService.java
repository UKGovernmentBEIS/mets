package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitTransferPreviewPermitDocumentService implements PermitPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService;

    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final PermitTransferBApplicationReviewRequestTaskPayload taskPayload =
                (PermitTransferBApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();

        final Permit permit = taskPayload.getPermit();
        final PermitType permitType = taskPayload.getPermitType();
        final PermitIssuanceGrantDetermination determination = (PermitIssuanceGrantDetermination) taskPayload.getDetermination();
        final LocalDate activationDate = determination.getActivationDate();
        final SortedMap<String, BigDecimal> annualEmissionsTargets = determination.getAnnualEmissionsTargets();
        final Map<UUID, String> attachments = taskPayload.getAttachments();

        final int consolidationNumber = 1; // consolidation number default value

        return permitPreviewCreatePermitDocumentService.getFile(
                decisionNotification,
                request,
                accountId,
                permit,
                permitType,
                activationDate,
                annualEmissionsTargets,
                attachments,
                consolidationNumber,
                Collections.emptyList()
        );
    }

    @Override
    public RequestTaskType getType() {
        return RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW;
    }
}
