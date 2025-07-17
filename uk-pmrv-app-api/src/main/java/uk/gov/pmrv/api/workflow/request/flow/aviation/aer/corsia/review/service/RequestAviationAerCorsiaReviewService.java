package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service.AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService;
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
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RequestAviationAerCorsiaReviewService {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestService requestService;
    private final AviationAerTradingSchemeValidatorService<AviationAerCorsiaContainer> aviationAerCorsiaValidatorService;
    private final AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService syncAerAttachmentsService;
    private final AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaSubmittedEmissionsCalculationService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;
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
    public void updateRequestPayloadWithReviewOutcome(RequestTask requestTask, AppUser appUser) {
        AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
            (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setReviewGroupDecisions(requestTaskPayload.getReviewGroupDecisions());
        requestPayload.setReviewAttachments(requestTaskPayload.getReviewAttachments());
        requestPayload.setReviewSectionsCompleted(requestTaskPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void updateRequestPayloadWithSkipReviewOutcome(final RequestTask requestTask, final AppUser appUser) {

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

        requestPayload.setRegulatorReviewer(appUser.getUserId());
    }

    @Transactional
    public void saveAerAmend(AviationAerCorsiaSaveApplicationAmendRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload taskPayload =
                (AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        syncAerAttachmentsService.sync(taskActionPayload.getReportingRequired(), taskPayload);

        taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());
        taskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());

        taskPayload.setReportingRequired(taskActionPayload.getReportingRequired());
        taskPayload.setReportingObligationDetails(taskActionPayload.getReportingObligationDetails());

        // Reset verification
        taskPayload.setVerificationPerformed(false);
    }

    @Transactional
    public void sendAmendedAerToVerifier(AviationAerApplicationRequestVerificationRequestTaskActionPayload taskActionPayload,
                                         RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        final AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            (AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        //validate aer
        AviationAerCorsiaContainer aerCorsiaContainer = aviationAerCorsiaReviewMapper
            .toAviationAerCorsiaContainer(amendsSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA);
        aviationAerCorsiaValidatorService.validateAer(aerCorsiaContainer);

        //calculate submitted emissions
        AviationAerCorsiaSubmittedEmissions submittedEmissions = calculateSubmittedEmissions(aerCorsiaContainer);

        //update request payload
        updateAviationAerCorsiaRequestPayloadUponAmendSubmit(requestPayload, amendsSubmitRequestTaskPayload, submittedEmissions);
        requestPayload.setAerSectionsCompleted(amendsSubmitRequestTaskPayload.getAerSectionsCompleted());
        requestPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(amendsSubmitRequestTaskPayload,
            submittedEmissions,
            request,
            appUser,
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER
        );
    }

    @Transactional
    public void sendAmendedAerToRegulator(AviationAerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload,
                                          RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload requestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        final AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
                (AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport and verification review decisions if verificationPerformed = false as in this case verification report is obsolete.
        if (!amendsSubmitRequestTaskPayload.isVerificationPerformed()) {
            requestPayload.setVerificationReport(null);
            AviationAerCorsiaReviewUtils.removeVerificationDataReviewGroupDecisionsFromRequestPayload(requestPayload);
        }

        //validate aer
        AviationAerCorsiaContainer aerCorsiaContainer = aviationAerCorsiaReviewMapper
                .toAviationAerCorsiaContainer(amendsSubmitRequestTaskPayload, requestPayload.getVerificationReport(), EmissionTradingScheme.CORSIA);
        aviationAerCorsiaValidatorService.validate(aerCorsiaContainer);

        //calculate submitted emissions
        AviationAerCorsiaSubmittedEmissions submittedEmissions = calculateSubmittedEmissions(aerCorsiaContainer);

        //update request payload
        updateAviationAerCorsiaRequestPayloadUponAmendSubmit(requestPayload, amendsSubmitRequestTaskPayload, submittedEmissions);
        requestPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());
        requestPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(
            amendsSubmitRequestTaskPayload,
            submittedEmissions,
            request,
            appUser,
            RequestActionType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED
        );

        //update request metadata with reportable emissions
        if(Boolean.TRUE.equals(aerCorsiaContainer.getReportingRequired())) {
            updateReportableEmissionsAndRequestMetadata(request, aerCorsiaContainer);
        } else {
            deleteReportableAndRequestMetadataEmissions(request, aerCorsiaContainer);
        }
    }

    private AviationAerCorsiaSubmittedEmissions calculateSubmittedEmissions(AviationAerCorsiaContainer aerCorsiaContainer) {
        return Boolean.TRUE.equals(aerCorsiaContainer.getReportingRequired())
            ? aviationAerCorsiaSubmittedEmissionsCalculationService.calculateSubmittedEmissions(aerCorsiaContainer)
            : null;
    }

    private void updateAviationAerCorsiaRequestPayloadUponAmendSubmit(AviationAerCorsiaRequestPayload requestPayload,
                                                       AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload,
                                                       AviationAerCorsiaSubmittedEmissions submittedEmissions) {
		AviationAerCorsiaReviewUtils.cleanUpDeprecatedAerDataReviewGroupDecisionsFromRequestPayload(
				requestPayload, amendsSubmitRequestTaskPayload.getReportingRequired());
		
		AviationAerCorsiaReviewUtils.cleanUpDeprecatedVerificationDataReviewGroupDecisionsFromRequestPayload(
				requestPayload, amendsSubmitRequestTaskPayload.getAer());
        
		requestPayload.setReportingRequired(amendsSubmitRequestTaskPayload.getReportingRequired());
		requestPayload.setReportingObligationDetails(amendsSubmitRequestTaskPayload.getReportingObligationDetails());
		requestPayload.setAer(amendsSubmitRequestTaskPayload.getAer());
		requestPayload.setAerAttachments(amendsSubmitRequestTaskPayload.getAerAttachments());
		requestPayload.setVerificationPerformed(amendsSubmitRequestTaskPayload.isVerificationPerformed());
		requestPayload.setAerMonitoringPlanVersions(amendsSubmitRequestTaskPayload.getAerMonitoringPlanVersions());
		requestPayload.setSubmittedEmissions(submittedEmissions);
        Optional.ofNullable(submittedEmissions).ifPresent(emissions -> {
            requestPayload.setTotalEmissionsProvided(emissions.getTotalEmissions().getAllFlightsEmissions());
            requestPayload.setTotalOffsetEmissionsProvided(emissions.getTotalEmissions().getOffsetFlightsEmissions());
        });
    }

    private void addApplicationSubmittedRequestAction(AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload,
                                                      AviationAerCorsiaSubmittedEmissions submittedEmissions,
                                                      Request request, AppUser appUser, RequestActionType requestActionType) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerCorsiaApplicationSubmittedRequestActionPayload aviationAerCorsiaApplicationSubmittedPayload =
            aviationAerCorsiaReviewMapper.toAviationAerCorsiaApplicationSubmittedRequestActionPayload(
                aviationAerAmendsSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        if (aviationAerAmendsSubmitRequestTaskPayload.isVerificationPerformed()) {
            aviationAerCorsiaApplicationSubmittedPayload.setVerificationReport(((AviationAerCorsiaRequestPayload) request.getPayload()).getVerificationReport());
        }
        requestService.addActionToRequest(request, aviationAerCorsiaApplicationSubmittedPayload, requestActionType, appUser.getUserId());
    }

    private void updateReportableEmissionsAndRequestMetadata(Request request, AviationAerCorsiaContainer aerCorsiaContainer) {
        AviationAerTotalReportableEmissions totalReportableEmissions =
                aviationReportableEmissionsService.updateReportableEmissions(aerCorsiaContainer, request.getAccountId(), false);
        AviationAerCorsiaTotalReportableEmissions totalEmissions = (AviationAerCorsiaTotalReportableEmissions) totalReportableEmissions;

        AviationAerCorsiaRequestMetadata metadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();
        metadata.setEmissions(totalReportableEmissions.getReportableEmissions());
        metadata.setTotalEmissionsOffsettingFlights(totalEmissions.getReportableOffsetEmissions());
        metadata.setTotalEmissionsClaimedReductions(totalEmissions.getReportableReductionClaimEmissions());

        Optional.ofNullable(aerCorsiaContainer.getVerificationReport()).ifPresent(report ->
    		metadata.setOverallAssessmentType(report.getVerificationData().getOverallDecision().getType()));
    }

    private void deleteReportableAndRequestMetadataEmissions(Request request, AviationAerCorsiaContainer aerCorsiaContainer) {
        aviationReportableEmissionsService.deleteReportableEmissions(request.getAccountId(), aerCorsiaContainer.getReportingYear());
        AviationAerRequestMetadata metadata = (AviationAerRequestMetadata) request.getMetadata();
        metadata.setEmissions(null);
        metadata.setOverallAssessmentType(null);
    }
}
