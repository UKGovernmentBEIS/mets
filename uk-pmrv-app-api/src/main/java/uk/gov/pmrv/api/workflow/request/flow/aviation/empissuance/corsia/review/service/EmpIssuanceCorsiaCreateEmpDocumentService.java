package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaCreateEmpDocumentService {

    private final RequestService requestService;

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    private final EmpCreateDocumentService empCreateDocumentService;

    public CompletableFuture<FileInfoDTO> create(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpIssuanceCorsiaRequestPayload requestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDecisionNotification().getSignatory();
        final Long accountId = request.getAccountId();
        final EmissionsMonitoringPlanCorsiaDTO emp = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return empCreateDocumentService.generateDocumentAsync(request,
                signatory,
                emp,
                DocumentTemplateType.EMP_CORSIA);
    }
}
