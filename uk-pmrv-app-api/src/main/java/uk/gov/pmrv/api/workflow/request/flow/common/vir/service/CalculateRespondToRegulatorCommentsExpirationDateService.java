package uk.gov.pmrv.api.workflow.request.flow.common.vir.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirExpirable;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@Service
@RequiredArgsConstructor
public class CalculateRespondToRegulatorCommentsExpirationDateService {

    private final RequestService requestService;

    private static final List<RequestTaskType> TASK_TYPES = List.of(
        RequestTaskType.VIR_RESPOND_TO_REGULATOR_COMMENTS,
        RequestTaskType.AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS
    ); 

    public Date calculateExpirationDate(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final RequestTask task = request.getRequestTasks().stream()
                .filter(requestTask -> TASK_TYPES.contains(requestTask.getType()))
                .findFirst().orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        final VirExpirable payload = (VirExpirable) task.getPayload();

        return calculateExpirationDate(payload.getRegulatorImprovementResponses());
    }

    private Date calculateExpirationDate(Map<String,RegulatorImprovementResponse> regulatorImprovementResponses) {
        final LocalDate expirationDate = regulatorImprovementResponses.values().stream()
                .filter(RegulatorImprovementResponse::isImprovementRequired)
                .map(RegulatorImprovementResponse::getImprovementDeadline)
                .min(LocalDate::compareTo).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return DateUtils.atEndOfDay(expirationDate);
    }
}
