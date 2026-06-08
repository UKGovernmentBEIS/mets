package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitIssuancePreviewPermitDocumentService implements PermitPreviewDocumentService {

    private final RequestTaskService requestTaskService;
    private final PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService;

    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final PermitIssuanceApplicationRequestTaskPayload taskPayload =
                (PermitIssuanceApplicationRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();

        final Permit permit = taskPayload.getPermit();
        final PermitType permitType = taskPayload.getPermitType();
        LocalDate activationDate = null;
        SortedMap<String, BigDecimal> annualEmissionsTargets = null;

        if (taskPayload instanceof PermitIssuanceApplicationReviewRequestTaskPayload reviewPayload
                && reviewPayload.getDetermination() instanceof PermitIssuanceGrantDetermination grantDetermination) {
            activationDate = grantDetermination.getActivationDate();
            annualEmissionsTargets = grantDetermination.getAnnualEmissionsTargets();
        }
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
    public List<RequestTaskType> getTypes() {
        return List.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW,
                RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT,
                RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT,
                RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW,
                RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_REVIEW,
                RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW,
                RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS);
    }
}
