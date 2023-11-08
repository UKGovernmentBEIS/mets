package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.mapper.PermitSurrenderMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@Service
@RequiredArgsConstructor
public class PermitSurrenderReviewGrantedService {
    
    private final RequestService requestService;
    private final InstallationAccountStatusService installationAccountStatusService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;

    private static final PermitSurrenderMapper permitSurrenderMapper = Mappers.getMapper(PermitSurrenderMapper.class);
    
    @Transactional
    public void executeGrantedPostActions(String requestId) {
        Request request = requestService.findRequestById(requestId);
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        Long accountId = request.getAccountId();
        
        // update account status
        installationAccountStatusService.handlePermitSurrenderGranted(accountId);
        
        PermitSurrenderApplicationGrantedRequestActionPayload grantedRequestActionPayload = 
                permitSurrenderMapper.toPermitSurrenderApplicationGrantedRequestActionPayload(requestPayload);

        DecisionNotification reviewDecisionNotification = requestPayload.getReviewDecisionNotification();

        grantedRequestActionPayload.setUsersInfo(requestActionUserInfoResolver
            .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request)
        );
        
        // add action
        requestService.addActionToRequest(request,
                grantedRequestActionPayload,
                RequestActionType.PERMIT_SURRENDER_APPLICATION_GRANTED,
                requestPayload.getRegulatorReviewer());
        
        //send official notice
        permitSurrenderOfficialNoticeService.sendReviewDeterminationOfficialNoticeForGranted(request);
    }
    
    public Date resolveNoticeReminderDate(String requestId) {
    	Request request = requestService.findRequestById(requestId);
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
		return DateUtils.convertLocalDateToDate(
				((PermitSurrenderReviewDeterminationGrant) requestPayload.getReviewDetermination()).getNoticeDate().minus(28, DAYS));
    }

    public Map<String, Object> getAerVariables(String requestId) {
        Map<String, Object> variables = new HashMap<>();

        Request request = requestService.findRequestById(requestId);
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        PermitSurrenderReviewDeterminationGrant reviewDeterminationGrant =
                (PermitSurrenderReviewDeterminationGrant) requestPayload.getReviewDetermination();

        variables.put(BpmnProcessConstants.AER_REQUIRED, reviewDeterminationGrant.getReportRequired());

        if(Boolean.TRUE.equals(reviewDeterminationGrant.getReportRequired())){
            variables.put(BpmnProcessConstants.AER_EXPIRATION_DATE,
                    DateUtils.convertLocalDateToDate(reviewDeterminationGrant.getReportDate()));
        }

        return variables;
    }
}
