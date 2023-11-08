package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.utils.AviationAerCorsiaReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestAviationAerCorsiaReviewService {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestService requestService;
    private final AviationAerTradingSchemeValidatorService<AviationAerCorsiaContainer> aviationAerCorsiaValidatorService;
    private final AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;

    @Transactional
    public void saveReviewGroupDecision(AviationAerCorsiaSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                        RequestTask requestTask) {

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = reviewRequestTaskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(taskActionPayload.getGroup(), taskActionPayload.getDecision());

        reviewRequestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void updateRequestPayloadWithReviewOutcome(RequestTask requestTask, PmrvUser pmrvUser) {
        AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        requestPayload.setReviewGroupDecisions(requestTaskPayload.getReviewGroupDecisions());
        requestPayload.setReviewAttachments(requestTaskPayload.getReviewAttachments());
        requestPayload.setReviewSectionsCompleted(requestTaskPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void updateRequestPayloadWithSkipReviewOutcome(final RequestTask requestTask, final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        final AviationAerCorsia aer = requestPayload.getAer();

        final Boolean reportingRequired = requestPayload.getReportingRequired();
        final Set<AviationAerCorsiaReviewGroup> aerDataReviewGroups =
            AviationAerCorsiaReviewGroup.getAerDataReviewGroups(reportingRequired);
        final Set<AviationAerCorsiaReviewGroup> verificationReportDataReviewGroups = requestPayload.isVerificationPerformed() ?
            AviationAerCorsiaReviewGroup.getVerificationReportDataReviewGroups(aer) :
            Set.of();

        final Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = requestPayload.getReviewGroupDecisions();

        for (final AviationAerCorsiaReviewGroup group : aerDataReviewGroups) {
            final AerDataReviewDecision decision = AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().build())
                .build();
            reviewGroupDecisions.put(group, decision);
        }
        for (final AviationAerCorsiaReviewGroup group : verificationReportDataReviewGroups) {
            final AerVerificationReportDataReviewDecision decision = AerVerificationReportDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().build())
                .build();
            reviewGroupDecisions.put(group, decision);
        }

        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
    }

    @Transactional
    public void saveAerAmend(AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setReportingRequired(taskActionPayload.getReportingRequired());
        taskPayload.setReportingObligationDetails(taskActionPayload.getReportingObligationDetails());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());

        // Reset verification
        taskPayload.setVerificationPerformed(false);
    }

    @Transactional
    public void sendAmendedAerToVerifier(AviationAerApplicationRequestVerificationRequestTaskActionPayload taskActionPayload,
                                         RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        final AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            (AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        //validate aer
        AviationAerCorsiaContainer aerCorsiaContainer = aviationAerCorsiaReviewMapper
            .toAviationAerCorsiaContainer(amendsSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA);
        aviationAerCorsiaValidatorService.validateAer(aerCorsiaContainer);

        //update request payload
        updateAviationAerCorsiaRequestPayload(requestPayload, amendsSubmitRequestTaskPayload);
        requestPayload.setAerSectionsCompleted(amendsSubmitRequestTaskPayload.getAerSectionsCompleted());
        requestPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(amendsSubmitRequestTaskPayload,
            request,
            pmrvUser,
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER
        );
    }

    private void updateAviationAerCorsiaRequestPayload(AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload,
                                                       AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload) {

        //clean up aer data related review groups that are deprecated after operator amends
        cleanUpDeprecatedAerDataReviewGroupDecisionsFromRequestPayload(aviationAerCorsiaRequestPayload, aviationAerAmendsSubmitRequestTaskPayload);

        aviationAerCorsiaRequestPayload.setReportingRequired(aviationAerAmendsSubmitRequestTaskPayload.getReportingRequired());
        aviationAerCorsiaRequestPayload.setReportingObligationDetails(aviationAerAmendsSubmitRequestTaskPayload.getReportingObligationDetails());
        aviationAerCorsiaRequestPayload.setAer(aviationAerAmendsSubmitRequestTaskPayload.getAer());
        aviationAerCorsiaRequestPayload.setAerAttachments(aviationAerAmendsSubmitRequestTaskPayload.getAerAttachments());
        aviationAerCorsiaRequestPayload.setVerificationPerformed(aviationAerAmendsSubmitRequestTaskPayload.isVerificationPerformed());
        aviationAerCorsiaRequestPayload.setAerMonitoringPlanVersions(aviationAerAmendsSubmitRequestTaskPayload.getAerMonitoringPlanVersions());
        aviationAerCorsiaRequestPayload.setReviewSectionsCompleted(aviationAerAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());
    }

    private void cleanUpDeprecatedAerDataReviewGroupDecisionsFromRequestPayload(AviationAerCorsiaRequestPayload requestPayload,
                                                                                AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {
        Set<AviationAerCorsiaReviewGroup> deprecatedAerDataReviewGroups =
            AviationAerCorsiaReviewUtils.getDeprecatedAerDataReviewGroups(requestPayload, amendsSubmitRequestTaskPayload);

        if (!deprecatedAerDataReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedAerDataReviewGroups);
        }
    }

    private void addApplicationSubmittedRequestAction(AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload,
                                                      Request request, PmrvUser pmrvUser, RequestActionType requestActionType) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerCorsiaApplicationSubmittedRequestActionPayload aviationAerCorsiaApplicationSubmittedPayload =
            aviationAerCorsiaReviewMapper.toAviationAerCorsiaApplicationSubmittedRequestActionPayload(
                aviationAerAmendsSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        requestService.addActionToRequest(request, aviationAerCorsiaApplicationSubmittedPayload, requestActionType, pmrvUser.getUserId());
    }
}
