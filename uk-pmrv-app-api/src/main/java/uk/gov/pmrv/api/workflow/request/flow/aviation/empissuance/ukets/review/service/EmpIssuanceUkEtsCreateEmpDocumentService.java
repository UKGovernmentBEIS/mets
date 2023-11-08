package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsCreateEmpDocumentService {

    private final RequestService requestService;

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    private final EmpCreateDocumentService empCreateDocumentService;

    public CompletableFuture<FileInfoDTO> create(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final EmpIssuanceUkEtsRequestPayload requestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDecisionNotification().getSignatory();
        final Long accountId = request.getAccountId();
        final EmissionsMonitoringPlanUkEtsDTO emp = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return empCreateDocumentService.generateDocumentAsync(request,
                signatory,
                emp,
                DocumentTemplateType.EMP_UKETS);
    }
}
