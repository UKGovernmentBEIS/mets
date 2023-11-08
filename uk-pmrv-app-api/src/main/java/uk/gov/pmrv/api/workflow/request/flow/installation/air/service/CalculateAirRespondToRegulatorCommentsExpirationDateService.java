package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@Service
@RequiredArgsConstructor
public class CalculateAirRespondToRegulatorCommentsExpirationDateService {

    private final RequestService requestService;

    public Date calculateExpirationDate(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestTask task = request.getRequestTasks().stream()
            .filter(requestTask -> requestTask.getType().equals(RequestTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS))
            .findFirst().orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        final AirApplicationRespondToRegulatorCommentsRequestTaskPayload payload =
            (AirApplicationRespondToRegulatorCommentsRequestTaskPayload) task.getPayload();

        return calculateExpirationDate(payload.getRegulatorImprovementResponses());
    }

    private Date calculateExpirationDate(
        final Map<Integer, RegulatorAirImprovementResponse> regulatorImprovementResponses) {

        LocalDate expirationDate = regulatorImprovementResponses.values().stream()
            .filter(RegulatorAirImprovementResponse::getImprovementRequired)
            .map(RegulatorAirImprovementResponse::getImprovementDeadline)
            .min(LocalDate::compareTo).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return Date.from(expirationDate.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
    }
}
