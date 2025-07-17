package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

@Service
public class PermitVariationRegulatorLedPreviewPermitDocumentService extends PermitVariationPreviewPermitDocumentBaseService implements PermitPreviewDocumentService {

    private final RequestTaskService requestTaskService;

    public PermitVariationRegulatorLedPreviewPermitDocumentService(
        final RequestTaskService requestTaskService,
        final PermitQueryService permitQueryService,
        final DateService dateService,
        final PermitVariationRequestQueryService permitVariationRequestQueryService,
        final PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService) {

        super(permitQueryService, dateService, permitVariationRequestQueryService, permitPreviewCreatePermitDocumentService);
        this.requestTaskService = requestTaskService;
    }

    @Transactional(readOnly = true)
    public FileDTO create(final Long taskId, final DecisionNotification decisionNotification) {

        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final Long accountId = request.getAccountId();

        final Permit permit = taskPayload.getPermit();
        final PermitType permitType = taskPayload.getPermitType();
        final PermitVariationRegulatorLedGrantDetermination determination = taskPayload.getDetermination();
        final LocalDate activationDate = determination.getActivationDate();
        final String logChanges = determination.getLogChanges();
        final Map<UUID, String> attachments = taskPayload.getAttachments();
        final SortedMap<String, BigDecimal> annualEmissionsTargets = determination.getAnnualEmissionsTargets();

        return this.getFile(decisionNotification, request, accountId, permit, permitType, activationDate, annualEmissionsTargets, attachments, logChanges);
    }

    @Override
    public RequestTaskType getType() {
        return RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT;
    }
}
