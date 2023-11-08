package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.ukets.validation.AviationAerUkEtsVerificationReportValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.utils.AviationAerUkEtsReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.mapper.AviationAerUkEtsVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestAviationAerUkEtsSubmitVerificationService {

    private final AviationAerUkEtsVerificationReportValidatorService ukEtsVerificationReportValidatorService;

    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationAerUkEtsVerifyMapper aviationAerUkEtsVerifyMapper;

    public void submitVerificationReport(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload taskPayload =
            (AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        //validate verification report
        ukEtsVerificationReportValidatorService.validate(taskPayload.getVerificationReport());

        // update request payload
        updateRequestPayload(request, taskPayload);

        // add request action
        addVerificationSubmittedRequestAction(taskPayload, request, pmrvUser);
    }

    private void updateRequestPayload(Request request, AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload) {
        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        //if verification has been performed and amends has been requested from the regulator,
        //clean up verification data related review groups that are deprecated after verifier amends
        if(aviationAerUkEtsRequestPayload.getVerificationReport() != null && !aviationAerUkEtsRequestPayload.getReviewGroupDecisions().isEmpty()) {
            cleanUpDeprecatedVerificationDataReviewGroupDecisionsFromRequestPayload(aviationAerUkEtsRequestPayload, verificationSubmitRequestTaskPayload);
        }

        aviationAerUkEtsRequestPayload.setVerificationReport(verificationSubmitRequestTaskPayload.getVerificationReport());
        aviationAerUkEtsRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aviationAerUkEtsRequestPayload.setVerificationPerformed(true);
        aviationAerUkEtsRequestPayload.setVerificationSectionsCompleted(verificationSubmitRequestTaskPayload.getVerificationSectionsCompleted());
        aviationAerUkEtsRequestPayload.setTotalEmissionsProvided(verificationSubmitRequestTaskPayload.getTotalEmissionsProvided());
        aviationAerUkEtsRequestPayload.setNotCoveredChangesProvided(verificationSubmitRequestTaskPayload.getNotCoveredChangesProvided());
    }

    private void addVerificationSubmittedRequestAction(AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload,
                                                      Request request, PmrvUser pmrvUser) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerUkEtsApplicationSubmittedRequestActionPayload aviationAerUkEtsApplicationSubmittedPayload =
            aviationAerUkEtsVerifyMapper.toAviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload(
                verificationSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD);

        requestService.addActionToRequest(
            request,
            aviationAerUkEtsApplicationSubmittedPayload,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED,
            pmrvUser.getUserId()
        );
    }

    private void cleanUpDeprecatedVerificationDataReviewGroupDecisionsFromRequestPayload(AviationAerUkEtsRequestPayload requestPayload,
                                                                                         AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload) {
        Set<AviationAerUkEtsReviewGroup> deprecatedVerificationDataReviewGroups =
            AviationAerUkEtsReviewUtils.getDeprecatedVerificationDataReviewGroups(requestPayload, verificationSubmitRequestTaskPayload);

        if (!deprecatedVerificationDataReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedVerificationDataReviewGroups);
        }
    }
}
