package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.service;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaCreateEmpDocumentService {

    private final RequestService requestService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final EmpCreateDocumentService createDocumentService;

    public CompletableFuture<FileInfoDTO> create(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        final String signatory = requestPayload.getDecisionNotification().getSignatory();
        final Long accountId = request.getAccountId();
        final EmissionsMonitoringPlanCorsiaDTO emp =
            emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanCorsiaDTOByAccountId(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return createDocumentService.generateDocumentAsync(request,
            signatory,
            emp,
            DocumentTemplateType.EMP_CORSIA);
    }
}
