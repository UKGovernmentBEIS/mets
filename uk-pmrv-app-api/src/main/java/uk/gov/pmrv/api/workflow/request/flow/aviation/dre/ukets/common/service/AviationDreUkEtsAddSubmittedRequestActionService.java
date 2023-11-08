package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.mapper.AviationDreUkEtsMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationDreUkEtsAddSubmittedRequestActionService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final RequestAccountContactQueryService requestAccountContactQueryService;

    private static final AviationDreUkEtsMapper DRE_UK_ETS_MAPPER = Mappers.getMapper(AviationDreUkEtsMapper.class);

    @Transactional
    public void add(final String requestId) {
        Request request = requestService.findRequestById(requestId);
        AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
        Optional<UserInfoDTO> requestAccountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        DecisionNotification notification = requestPayload.getDecisionNotification();

        // if there isn't primary contact defined in the account then there can't be any other operator users, thus usersInfo may contain only signatory info.
        Map<String, RequestActionUserInfo> usersInfo = requestAccountPrimaryContact.isPresent() ?
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(), request) :
            Map.of(notification.getSignatory(), requestActionUserInfoResolver.getSignatoryUserInfo(notification.getSignatory()));

        AviationDreApplicationSubmittedRequestActionPayload actionPayload = DRE_UK_ETS_MAPPER.toSubmittedActionPayload(requestPayload, usersInfo);

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED,
            requestPayload.getRegulatorAssignee());
    }
}
