package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.mapper.AviationDoECorsiaMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.validation.AviationDoECorsiaValidationService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaSubmitService {

    private final RequestService requestService;
    private final AviationDoECorsiaValidationService validatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final AviationDoECorsiaMapper AVIATION_DOE_CORSIA_MAPPER = Mappers.getMapper(AviationDoECorsiaMapper.class);

    @Transactional
    public void applySaveAction(
                AviationDoECorsiaSubmitSaveRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {

        AviationDoECorsiaApplicationSubmitRequestTaskPayload taskPayload =
                (AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDoe(taskActionPayload.getDoe());
        taskPayload.setSectionCompleted(taskActionPayload.getSectionCompleted());
    }

    @Transactional
	public void addCancelledRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.AVIATION_DOE_CORSIA_SUBMIT_CANCELLED,
            request.getPayload().getRegulatorAssignee());

	}

    @Transactional
    public void applySubmitNotify(RequestTask requestTask, DecisionNotification decisionNotification, AppUser appUser) {

        final AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final AviationDoECorsia doe = requestTaskPayload.getDoe();

        validatorService.validateAviationDoECorsia(doe);

        if(!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final AviationDoECorsiaRequestPayload requestPayload =
                (AviationDoECorsiaRequestPayload) requestTask.getRequest().getPayload();

        final Long accountId = requestTask.getRequest().getAccountId();

        AviationAccountInfoDTO
            aviationAccountInfoDTO = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);

        if (aviationAccountInfoDTO.getLocation() == null) {
            throw new BusinessException(MetsErrorCode.AVIATION_ACCOUNT_LOCATION_NOT_EXIST);
        }
        requestPayload.setDecisionNotification(decisionNotification);
        updateRequestPayloadWithSubmitRequestTaskPayload(requestPayload, requestTaskPayload);
    }

    @Transactional
    public void addSubmittedRequestAction(final String requestId) {
        Request request = requestService.findRequestById(requestId);
        AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
        Optional<UserInfoDTO> requestAccountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        DecisionNotification notification = requestPayload.getDecisionNotification();

        // if there isn't primary contact defined in the account then there can't be any other operator users, thus usersInfo may contain only signatory info.
        Map<String, RequestActionUserInfo> usersInfo = requestAccountPrimaryContact.isPresent() ?
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(), request) :
            Map.of(notification.getSignatory(), requestActionUserInfoResolver.getSignatoryUserInfo(notification.getSignatory()));

        AviationDoECorsiaSubmittedRequestActionPayload actionPayload =
                AVIATION_DOE_CORSIA_MAPPER.toSubmittedActionPayload(requestPayload, usersInfo);

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.AVIATION_DOE_CORSIA_SUBMITTED,
            requestPayload.getRegulatorAssignee());
    }

    @Transactional
    public void requestPeerReview(RequestTask requestTask, String peerReviewer) {
        final AviationDoECorsiaRequestPayload requestPayload =
                (AviationDoECorsiaRequestPayload) requestTask.getRequest().getPayload();
        final AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            (AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        updateRequestPayloadWithSubmitRequestTaskPayload(requestPayload, requestTaskPayload);
    }

    private void updateRequestPayloadWithSubmitRequestTaskPayload(final AviationDoECorsiaRequestPayload requestPayload,
                                                                  final AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload) {
        final AviationDoECorsia doe = requestTaskPayload.getDoe();
        requestPayload.setDoe(doe);
        requestPayload.setSectionCompleted(requestTaskPayload.getSectionCompleted());
        requestPayload.setDoeAttachments(requestTaskPayload.getDoeAttachments());
    }
}
