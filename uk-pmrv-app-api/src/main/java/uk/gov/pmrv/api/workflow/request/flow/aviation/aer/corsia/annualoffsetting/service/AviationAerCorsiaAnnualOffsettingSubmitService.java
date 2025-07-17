package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service.AviationAerCorsiaAnnualOffsettingValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingSubmitService {

    private final RequestService requestService;
    private final AviationAerCorsiaAnnualOffsettingValidatorService aviationAerCorsiaAnnualOffsettingValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void applySaveAction(RequestTask requestTask, AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload taskActionPayload) {
        final AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = taskActionPayload.getAviationAerCorsiaAnnualOffsetting();
        aviationAerCorsiaAnnualOffsettingValidatorService.validateAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);

        final AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setAviationAerCorsiaAnnualOffsettingSectionsCompleted(taskActionPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
        taskPayload.setAviationAerCorsiaAnnualOffsetting(taskActionPayload.getAviationAerCorsiaAnnualOffsetting());
    }

    public void cancel(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_CANCELLED,
                request.getPayload().getRegulatorAssignee());
    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = (AviationAerCorsiaAnnualOffsettingRequestPayload) requestTask.getRequest().getPayload();
        final AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                (AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = requestTaskPayload.getAviationAerCorsiaAnnualOffsetting();
        requestPayload.setAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
        requestPayload.setAviationAerCorsiaAnnualOffsettingSectionsCompleted(
                requestTaskPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
    }

    public RequestType getRequestType() {
        return RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING;
    }

    public void applySubmitNotify(RequestTask requestTask, DecisionNotification decisionNotification, AppUser appUser) {
        final AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                (AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload) requestTask
                .getPayload();
        final AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = requestTaskPayload.getAviationAerCorsiaAnnualOffsetting();

        aviationAerCorsiaAnnualOffsettingValidatorService.validateAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final AviationAerCorsiaAnnualOffsettingRequestMetadata requestMetadata = (AviationAerCorsiaAnnualOffsettingRequestMetadata) requestTask.getRequest().getMetadata();
        final AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = (AviationAerCorsiaAnnualOffsettingRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setDecisionNotification(decisionNotification);

        requestPayload.setAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
        requestPayload.setAviationAerCorsiaAnnualOffsettingSectionsCompleted(requestTaskPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());

        requestMetadata.setCalculatedAnnualOffsetting(requestTaskPayload.getAviationAerCorsiaAnnualOffsetting().getCalculatedAnnualOffsetting());
    }
}
