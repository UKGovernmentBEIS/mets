package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service.AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.utils.AviationAerUkEtsReviewUtils;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestAviationAerUkEtsReviewService {

    private final AviationAerTradingSchemeValidatorService<AviationAerUkEtsContainer> aviationAerUkEtsValidatorService;
    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;
    private final AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;
    private final AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService syncAerAttachmentsService;
    private final AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Transactional
    public void saveReviewGroupDecision(AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                        RequestTask requestTask) {

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (AviationAerUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = reviewRequestTaskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(taskActionPayload.getGroup(), taskActionPayload.getDecision());

        reviewRequestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void updateRequestPayloadWithReviewOutcome(RequestTask requestTask, AppUser appUser) {
        AviationAerUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            (AviationAerUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setReviewGroupDecisions(requestTaskPayload.getReviewGroupDecisions());
        requestPayload.setReviewAttachments(requestTaskPayload.getReviewAttachments());
        requestPayload.setReviewSectionsCompleted(requestTaskPayload.getReviewSectionsCompleted());
    }

    @Transactional
    public void updateRequestPayloadWithSkipReviewOutcome(final RequestTask requestTask, final AppUser appUser) {
        
        final Request request = requestTask.getRequest();
        final AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        final AviationAerUkEts aer = requestPayload.getAer();
        
        final Boolean reportingRequired = requestPayload.getReportingRequired();
        final Set<AviationAerUkEtsReviewGroup> aerDataReviewGroups = 
            AviationAerUkEtsReviewGroup.getAerDataReviewGroups(aer, reportingRequired);
        final AviationAerUkEtsVerificationReport verificationReport = requestPayload.getVerificationReport();
        final Set<AviationAerUkEtsReviewGroup> verificationReportDataReviewGroups = requestPayload.isVerificationPerformed() ?
            AviationAerUkEtsReviewGroup.getVerificationReportDataReviewGroups(verificationReport) :
            Set.of();

        final Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = requestPayload.getReviewGroupDecisions();
        
        for (final AviationAerUkEtsReviewGroup group : aerDataReviewGroups) {
            final AerDataReviewDecision decision = AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().build())
                .build();
            reviewGroupDecisions.put(group, decision);
        }
        for (final AviationAerUkEtsReviewGroup group : verificationReportDataReviewGroups) {
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
    public void saveAerAmend(AviationAerUkEtsSaveApplicationAmendRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

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
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        final AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            (AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        //validate aer
        AviationAerUkEtsContainer aerUkEtsContainer = aviationAerUkEtsReviewMapper
            .toAviationAerUkEtsContainer(amendsSubmitRequestTaskPayload, EmissionTradingScheme.UK_ETS_AVIATION);
        aviationAerUkEtsValidatorService.validateAer(aerUkEtsContainer);

        //calculate submitted emissions
        AviationAerUkEtsSubmittedEmissions submittedEmissions = calculateSubmittedEmissions(aerUkEtsContainer);

        //update request payload
        updateAviationAerUkEtsRequestPayload(requestPayload, amendsSubmitRequestTaskPayload, submittedEmissions);
        requestPayload.setAerSectionsCompleted(amendsSubmitRequestTaskPayload.getAerSectionsCompleted());
        requestPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(amendsSubmitRequestTaskPayload,
            submittedEmissions,
            request,
            appUser,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER
        );
    }

    @Transactional
    public void sendAmendedAerToRegulator(AviationAerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload,
                                          RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        final AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            (AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport and verification review decisions if verificationPerformed = false as in this case verification report is obsolete.
        if (!amendsSubmitRequestTaskPayload.isVerificationPerformed()) {
            requestPayload.setVerificationReport(null);
            cleanUpVerificationDataReviewGroupDecisionsFromRequestPayload(requestPayload);
        }

        //validate aer
        AviationAerUkEtsContainer aerUkEtsContainer = aviationAerUkEtsReviewMapper
            .toAviationAerUkEtsContainer(amendsSubmitRequestTaskPayload, requestPayload.getVerificationReport(), EmissionTradingScheme.UK_ETS_AVIATION);
        aviationAerUkEtsValidatorService.validate(aerUkEtsContainer);

        AviationAerUkEtsSubmittedEmissions submittedEmissions = calculateSubmittedEmissions(aerUkEtsContainer);

        //update request payload
        updateAviationAerUkEtsRequestPayload(requestPayload, amendsSubmitRequestTaskPayload, submittedEmissions);
        requestPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());
        requestPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestPayload.setVerificationSectionsCompleted(taskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(amendsSubmitRequestTaskPayload,
            submittedEmissions,
            request,
            appUser,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED
        );

        //update request metadata with reportable emissions
        if(Boolean.TRUE.equals(aerUkEtsContainer.getReportingRequired())) {
            updateReportableEmissionsAndRequestMetadata(request, aerUkEtsContainer);
        } else {
            deleteReportableAndRequestMetadataEmissions(request, aerUkEtsContainer);
        }
    }

    private AviationAerUkEtsSubmittedEmissions calculateSubmittedEmissions(AviationAerUkEtsContainer aerUkEtsContainer) {
        return Boolean.TRUE.equals(aerUkEtsContainer.getReportingRequired())
            ? aviationAerUkEtsEmissionsCalculationService.calculateSubmittedEmissions(aerUkEtsContainer)
            : null;
    }

    private void updateAviationAerUkEtsRequestPayload(AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload,
                                                      AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload,
                                                      AviationAerUkEtsSubmittedEmissions submittedEmissions) {

        //clean up aer data related review groups that are deprecated after operator amends
        cleanUpDeprecatedAerDataReviewGroupDecisionsFromRequestPayload(aviationAerUkEtsRequestPayload, aviationAerAmendsSubmitRequestTaskPayload);

        aviationAerUkEtsRequestPayload.setReportingRequired(aviationAerAmendsSubmitRequestTaskPayload.getReportingRequired());
        aviationAerUkEtsRequestPayload.setReportingObligationDetails(aviationAerAmendsSubmitRequestTaskPayload.getReportingObligationDetails());
        aviationAerUkEtsRequestPayload.setAer(aviationAerAmendsSubmitRequestTaskPayload.getAer());
        aviationAerUkEtsRequestPayload.setAerAttachments(aviationAerAmendsSubmitRequestTaskPayload.getAerAttachments());
        aviationAerUkEtsRequestPayload.setVerificationPerformed(aviationAerAmendsSubmitRequestTaskPayload.isVerificationPerformed());
        aviationAerUkEtsRequestPayload.setAerMonitoringPlanVersions(aviationAerAmendsSubmitRequestTaskPayload.getAerMonitoringPlanVersions());
        aviationAerUkEtsRequestPayload.setReviewSectionsCompleted(aviationAerAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());

        aviationAerUkEtsRequestPayload.setSubmittedEmissions(submittedEmissions);
    }

    private void cleanUpDeprecatedAerDataReviewGroupDecisionsFromRequestPayload(AviationAerUkEtsRequestPayload requestPayload,
                                                                                AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload) {
        Set<AviationAerUkEtsReviewGroup> deprecatedAerDataReviewGroups =
            AviationAerUkEtsReviewUtils.getDeprecatedAerDataReviewGroups(requestPayload, amendsSubmitRequestTaskPayload);

        if (!deprecatedAerDataReviewGroups.isEmpty()) {
            requestPayload.getReviewGroupDecisions().keySet().removeAll(deprecatedAerDataReviewGroups);
        }
    }

    private void addApplicationSubmittedRequestAction(AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload aviationAerAmendsSubmitRequestTaskPayload,
                                                      AviationAerUkEtsSubmittedEmissions submittedEmissions,
                                                      Request request, AppUser appUser, RequestActionType requestActionType) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerUkEtsApplicationSubmittedRequestActionPayload aviationAerUkEtsApplicationSubmittedPayload =
            aviationAerUkEtsReviewMapper.toAviationAerUkEtsApplicationSubmittedRequestActionPayload(
                aviationAerAmendsSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);
        if (aviationAerAmendsSubmitRequestTaskPayload.isVerificationPerformed()) {
            aviationAerUkEtsApplicationSubmittedPayload.setVerificationReport(((AviationAerUkEtsRequestPayload) request.getPayload()).getVerificationReport());
        }
        requestService.addActionToRequest(request, aviationAerUkEtsApplicationSubmittedPayload, requestActionType, appUser.getUserId());
    }

    private void cleanUpVerificationDataReviewGroupDecisionsFromRequestPayload(AviationAerUkEtsRequestPayload requestPayload) {
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> verificationDataReviewGroupDecisions = requestPayload.getReviewGroupDecisions().entrySet().stream()
            .filter(entry -> entry.getValue().getReviewDataType() == AerReviewDataType.VERIFICATION_REPORT_DATA)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        requestPayload.getReviewGroupDecisions()
            .keySet()
            .removeAll(verificationDataReviewGroupDecisions.keySet());
    }

    private void updateReportableEmissionsAndRequestMetadata(Request request, AviationAerUkEtsContainer aerUkEtsContainer) {
        AviationAerTotalReportableEmissions totalReportableEmissions =
            aviationReportableEmissionsService.updateReportableEmissions(aerUkEtsContainer, request.getAccountId(), false);

        AviationAerRequestMetadata metadata = (AviationAerRequestMetadata) request.getMetadata();
        metadata.setEmissions(totalReportableEmissions.getReportableEmissions());
        
        Optional.ofNullable(aerUkEtsContainer.getVerificationReport()).ifPresent(report ->
        	metadata.setOverallAssessmentType(report.getVerificationData().getOverallDecision().getType()));
    }

    private void deleteReportableAndRequestMetadataEmissions(Request request, AviationAerUkEtsContainer aerUkEtsContainer) {
        aviationReportableEmissionsService.deleteReportableEmissions(request.getAccountId(), aerUkEtsContainer.getReportingYear());
        AviationAerRequestMetadata metadata = (AviationAerRequestMetadata) request.getMetadata();
        metadata.setEmissions(null);
        metadata.setOverallAssessmentType(null);
    }
}
