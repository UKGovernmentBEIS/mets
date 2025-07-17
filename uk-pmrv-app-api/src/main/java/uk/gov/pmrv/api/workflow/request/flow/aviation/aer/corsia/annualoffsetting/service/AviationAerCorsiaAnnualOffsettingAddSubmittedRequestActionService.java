package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.mapper.AviationAerCorsiaAnnualOffsettingMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;


@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingAddSubmittedRequestActionService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final AviationAerCorsiaAnnualOffsettingMapper MAPPER =
            Mappers.getMapper(AviationAerCorsiaAnnualOffsettingMapper.class);

    @Transactional
    public void add(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload =
                (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();

        final AviationAerCorsiaAnnualOffsettingApplicationSubmittedRequestActionPayload actionPayload =
                MAPPER.toSubmittedActionPayload(requestPayload);

        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED,
                requestPayload.getRegulatorAssignee());
    }
}
