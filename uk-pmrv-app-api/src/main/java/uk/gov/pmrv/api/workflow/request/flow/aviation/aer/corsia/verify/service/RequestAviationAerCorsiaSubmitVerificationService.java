package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.corsia.validation.AviationAerCorsiaVerificationReportValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.utils.AviationAerCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.mapper.AviationAerCorsiaVerifyMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

@Service
@RequiredArgsConstructor
public class RequestAviationAerCorsiaSubmitVerificationService {

    private final AviationAerCorsiaVerificationReportValidatorService corsiaVerificationReportValidatorService;

    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationAerCorsiaVerifyMapper aviationAerCorsiaVerifyMapper;

    public void submitVerificationReport(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload taskPayload =
            (AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        //validate verification report
        corsiaVerificationReportValidatorService.validate(taskPayload.getVerificationReport(), taskPayload.getAer());

        // update request payload
        updateRequestPayload(request, taskPayload);

        // add request action
        addVerificationSubmittedRequestAction(taskPayload, request, appUser);
    }

    private void updateRequestPayload(Request request, AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload) {
        AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        /*
        Implementation note when the regulator review is done:
        If verification has been performed and amends has been requested from the regulator,
        clean up verification data related review groups that are deprecated after verifier amends
         */
		if (aviationAerCorsiaRequestPayload.getVerificationReport() != null
				&& !aviationAerCorsiaRequestPayload.getReviewGroupDecisions().isEmpty()) {
			AviationAerCorsiaReviewUtils.cleanUpDeprecatedVerificationDataReviewGroupDecisionsFromRequestPayload(
					aviationAerCorsiaRequestPayload, verificationSubmitRequestTaskPayload.getAer());
		}

        aviationAerCorsiaRequestPayload.setVerificationReport(verificationSubmitRequestTaskPayload.getVerificationReport());
        aviationAerCorsiaRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aviationAerCorsiaRequestPayload.setVerificationPerformed(true);
        aviationAerCorsiaRequestPayload.setVerificationSectionsCompleted(verificationSubmitRequestTaskPayload.getVerificationSectionsCompleted());
        aviationAerCorsiaRequestPayload.setTotalEmissionsProvided(verificationSubmitRequestTaskPayload.getTotalEmissionsProvided());
        aviationAerCorsiaRequestPayload.setTotalOffsetEmissionsProvided(verificationSubmitRequestTaskPayload.getTotalOffsetEmissionsProvided());
    }

    private void addVerificationSubmittedRequestAction(AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload verificationSubmitRequestTaskPayload,
                                                      Request request, AppUser appUser) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerCorsiaApplicationSubmittedRequestActionPayload aviationAerCorsiaApplicationSubmittedPayload =
            aviationAerCorsiaVerifyMapper.toAviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload(
                verificationSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD);

        requestService.addActionToRequest(
            request,
            aviationAerCorsiaApplicationSubmittedPayload,
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED,
            appUser.getUserId()
        );
    }

}
