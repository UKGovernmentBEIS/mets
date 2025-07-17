package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation.AviationAerCorsia3YearPeriodOffsettingValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;


@Service
@RequiredArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingSubmitService {

    private final RequestService requestService;
    private final AviationAerCorsia3YearPeriodOffsettingValidator validator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void applySaveAction(RequestTask requestTask,
                                AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload taskActionPayload) {

        final AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        validator.validate3YearPeriodOffsetting(taskActionPayload.getAviationAerCorsia3YearPeriodOffsetting());

        taskPayload.setAviationAerCorsia3YearPeriodOffsettingSectionsCompleted(taskActionPayload
                .getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted());

        taskPayload
                .setAviationAerCorsia3YearPeriodOffsetting(taskActionPayload
                        .getAviationAerCorsia3YearPeriodOffsetting());

    }

    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {

        final AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                (AviationAerCorsia3YearPeriodOffsettingRequestPayload) requestTask.getRequest().getPayload();
        final AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                requestTaskPayload.getAviationAerCorsia3YearPeriodOffsetting();
        requestPayload.setAviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        requestPayload.setAviationAerCorsia3YearPeriodOffsettingSectionsCompleted(
                requestTaskPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted());
    }

    public void cancel(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_CANCELLED,
                request.getPayload().getRegulatorAssignee());
    }

    public void applySubmitNotify(RequestTask requestTask,
                                  DecisionNotification decisionNotification,
                                  AppUser appUser) {
        final AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) requestTask
                .getPayload();
        final AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                requestTaskPayload.getAviationAerCorsia3YearPeriodOffsetting();

        validator.validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }


        final AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                (AviationAerCorsia3YearPeriodOffsettingRequestPayload) requestTask.getRequest().getPayload();

        final AviationAerCorsia3YearPeriodOffsettingRequestMetadata requestMetadata =
                (AviationAerCorsia3YearPeriodOffsettingRequestMetadata) requestTask.getRequest().getMetadata();

        requestPayload.setDecisionNotification(decisionNotification);

        requestPayload.setAviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        requestPayload
                .setAviationAerCorsia3YearPeriodOffsettingSectionsCompleted(
                        requestTaskPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted());


        requestMetadata.setPeriodOffsettingRequirements(
                aviationAerCorsia3YearPeriodOffsetting
                        .getPeriodOffsettingRequirements());

        requestMetadata.setOperatorHaveOffsettingRequirements(
                aviationAerCorsia3YearPeriodOffsetting
                        .getOperatorHaveOffsettingRequirements());

    }


    public RequestType getRequestType() {
        return RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING;
    }

}
