package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestAerSubmitService {

    private final AerValidatorService aerValidatorService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final RequestService requestService;
    private final AerService aerService;

    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    public void sendToRegulator(RequestTask requestTask, PmrvUser pmrvUser) {
        processRegulatorAerSubmission(requestTask, pmrvUser);
    }

    public void sendAmendsToRegulator(RequestTask requestTask,
                                      AerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload,
                                      PmrvUser pmrvUser) {
        processRegulatorAerAmendsSubmission(requestTask, pmrvUser, taskActionPayload.getAerSectionsCompleted());
    }

    public void sendToVerifier(AerApplicationRequestVerificationRequestTaskActionPayload actionPayload,
                               RequestTask requestTask, PmrvUser pmrvUser) {
        processAerSubmissionToVerifier(actionPayload, requestTask, pmrvUser);
    }

    public void sendAmendsToVerifier(AerApplicationRequestVerificationRequestTaskActionPayload actionPayload,
                                     RequestTask requestTask, PmrvUser pmrvUser) {
        processAerSubmissionToVerifierForAmends(actionPayload, requestTask, pmrvUser);
    }

    public void sendToOperator(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerApplicationVerificationSubmitRequestTaskPayload taskPayload =
            (AerApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        //Validate Aer verification report
        aerValidatorService.validateVerificationReport(taskPayload.getVerificationReport(),
            taskPayload.getPermitOriginatedData());
        // Update request payload
        aerRequestPayload.setVerificationReport(taskPayload.getVerificationReport());
        aerRequestPayload.setVerificationPerformed(true);
        aerRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aerRequestPayload.setVerificationSectionsCompleted(taskPayload.getVerificationSectionsCompleted());
        aerRequestPayload.setVerifiedAer(taskPayload.getAer());
        aerRequestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());

        // Add submitted action
        InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(
                request.getAccountId());
        AerApplicationVerificationSubmittedRequestActionPayload actionPayload =
            aerMapper.toAerApplicationVerificationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.AER_APPLICATION_VERIFICATION_SUBMITTED,
            pmrvUser.getUserId());
    }

    private void submitAer(AerRequestPayload aerRequestPayload, RequestTask requestTask,
                           PmrvUser pmrvUser, RequestActionType requestActionType, RequestActionPayload actionPayload) {
        final AerApplicationSubmitRequestTaskPayload taskPayload =
            (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        aerRequestPayload.setAer(taskPayload.getAer());
        aerRequestPayload.setAerAttachments(taskPayload.getAerAttachments());
        aerRequestPayload.setAerSectionsCompleted(taskPayload.getAerSectionsCompleted());
        aerRequestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());
        aerRequestPayload.setPermitOriginatedData(taskPayload.getPermitOriginatedData());
        aerRequestPayload.setMonitoringPlanVersions(taskPayload.getMonitoringPlanVersions());

        requestService.addActionToRequest(
            requestTask.getRequest(),
            actionPayload,
            requestActionType,
            pmrvUser.getUserId());
    }

    private void submitAerForAmends(AerRequestPayload aerRequestPayload, RequestTask requestTask,
                                    Map<String, List<Boolean>> aerSectionsCompleted, PmrvUser pmrvUser,
                                    RequestActionType requestActionType, RequestActionPayload actionPayload) {
        final AerApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        aerRequestPayload.setAer(taskPayload.getAer());
        aerRequestPayload.setAerAttachments(taskPayload.getAerAttachments());
        aerRequestPayload.setAerSectionsCompleted(aerSectionsCompleted);
        aerRequestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());
        aerRequestPayload.setPermitOriginatedData(taskPayload.getPermitOriginatedData());
        aerRequestPayload.setMonitoringPlanVersions(taskPayload.getMonitoringPlanVersions());
        aerRequestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());

        requestService.addActionToRequest(
            requestTask.getRequest(),
            actionPayload,
            requestActionType,
            pmrvUser.getUserId());
    }

    private AerApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(RequestTask requestTask, AerApplicationSubmitRequestTaskPayload taskPayload) {
        InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(
                requestTask.getRequest().getAccountId());

        AerApplicationSubmittedRequestActionPayload actionPayload =
            aerMapper.toAerApplicationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        return actionPayload;
    }

    private AerApplicationAmendsSubmittedRequestActionPayload createApplicationAmendsSubmittedRequestActionPayload(RequestTask requestTask, AerApplicationSubmitRequestTaskPayload taskPayload) {
        InstallationOperatorDetails installationOperatorDetails =
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(
                requestTask.getRequest().getAccountId());

        AerApplicationAmendsSubmittedRequestActionPayload actionPayload =
            aerMapper.toAerApplicationAmendsSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        return actionPayload;
    }

    private void updateRequestMetadata(Request request, AerApplicationSubmitRequestTaskPayload taskPayload,
                                       AerRequestPayload aerRequestPayload) {
        // Update Reportable Emissions
        final AerRequestMetadata metadata = (AerRequestMetadata) request.getMetadata();
        AerContainer aerContainer = aerMapper.toAerContainer(aerRequestPayload,
            taskPayload.getInstallationOperatorDetails(), metadata);
        AerSubmitParams params = AerSubmitParams.builder()
            .accountId(request.getAccountId())
            .aerContainer(aerContainer)
            .build();
        BigDecimal totalEmissions = aerService.updateReportableEmissions(params);

        // Save verifier decision and total emissions in request metadata
        Optional.ofNullable(aerContainer.getVerificationReport()).ifPresent(report ->
            metadata.setOverallAssessmentType(report.getVerificationData().getOverallAssessment().getType()));
        metadata.setEmissions(totalEmissions);
    }

    private AerApplicationSubmitRequestTaskPayload handleAmendsAerSubmission(RequestTask requestTask,
                                                                             Request request,
                                                                             AerRequestPayload aerRequestPayload) {
        final AerApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport if verificationPerformed = false as in this case verification report is obsolete.
        // If it is mandatory it is handled by VerificationPerformedRequestTaskActionValidator
        if (!taskPayload.isVerificationPerformed()) {
            aerRequestPayload.setVerificationReport(null);
            clearVerifierReviewGroupDecisions(aerRequestPayload);
        }
        // Validate AER and Verification report
        AerContainer container = aerMapper.toAerContainer(taskPayload, aerRequestPayload.getVerificationReport());
        aerValidatorService.validate(container, request.getAccountId());
        return taskPayload;
    }

    private AerApplicationSubmitRequestTaskPayload handleAerSubmission(RequestTask requestTask,
                                                                       Request request,
                                                                       AerRequestPayload aerRequestPayload) {
        final AerApplicationSubmitRequestTaskPayload taskPayload =
            (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport if verificationPerformed = false as in this case verification report is obsolete.
        // If it is mandatory it is handled by VerificationPerformedRequestTaskActionValidator
        if (!taskPayload.isVerificationPerformed()) {
            aerRequestPayload.setVerificationReport(null);
        }
        // Validate AER and Verification report
        AerContainer container = aerMapper.toAerContainer(taskPayload, aerRequestPayload.getVerificationReport());
        aerValidatorService.validate(container, request.getAccountId());
        return taskPayload;
    }

    private void clearVerifierReviewGroupDecisions(AerRequestPayload aerRequestPayload) {
        Map<AerReviewGroup, AerReviewDecision> verifierReviewGroupDecisions =
            aerRequestPayload.getReviewGroupDecisions().entrySet().stream()
                .filter(entry -> entry.getValue().getReviewDataType() == AerReviewDataType.VERIFICATION_REPORT_DATA)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        aerRequestPayload.getReviewGroupDecisions()
            .keySet()
            .removeAll(verifierReviewGroupDecisions.keySet());
    }

    private void processRegulatorAerSubmission(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        final AerApplicationSubmitRequestTaskPayload taskPayload = handleAerSubmission(requestTask, request,
            aerRequestPayload);

        RequestActionPayload actionPayload;
        RequestActionType requestActionType;

        actionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload);
        requestActionType = RequestActionType.AER_APPLICATION_SUBMITTED;


        // Submit AER
        submitAer(aerRequestPayload, requestTask, pmrvUser, requestActionType, actionPayload);

        // Update Reportable Emissions
        updateRequestMetadata(request, taskPayload, aerRequestPayload);
    }

    private void processRegulatorAerAmendsSubmission(RequestTask requestTask, PmrvUser pmrvUser, Map<String,
        List<Boolean>> aerSectionsCompleted) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        final AerApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (AerApplicationAmendsSubmitRequestTaskPayload) handleAmendsAerSubmission(requestTask, request,
                aerRequestPayload);

        RequestActionPayload actionPayload;
        RequestActionType requestActionType;

        actionPayload = createApplicationAmendsSubmittedRequestActionPayload(requestTask, taskPayload);
        requestActionType = RequestActionType.AER_APPLICATION_AMENDS_SUBMITTED;

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the aer
        //this should be done before setting the aer object of the requestPayload with the corresponding from the task payload
        cleanUpDeprecatedReviewGroupDecisions(aerRequestPayload, taskPayload.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().keySet());

        // Submit AER
        submitAerForAmends(aerRequestPayload, requestTask, aerSectionsCompleted == null ?
                taskPayload.getAerSectionsCompleted() : aerSectionsCompleted, pmrvUser, requestActionType,
            actionPayload);

        // Update Reportable Emissions
        updateRequestMetadata(request, taskPayload, aerRequestPayload);
    }

    private void processAerSubmissionToVerifier(AerApplicationRequestVerificationRequestTaskActionPayload actionPayload,
                                                RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        final AerApplicationSubmitRequestTaskPayload taskPayload =
            (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        RequestActionPayload requestActionPayload;
        RequestActionType requestActionType;

        requestActionPayload =
            createApplicationSubmittedRequestActionPayload(requestTask, taskPayload);
        requestActionType = RequestActionType.AER_APPLICATION_SENT_TO_VERIFIER;

        // Validate AER
        AerContainer container = aerMapper.toAerContainer(taskPayload);
        aerValidatorService.validateAer(container);

        // Update verification sections completed
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        aerRequestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        // Submit AER
        submitAer(aerRequestPayload, requestTask, pmrvUser, requestActionType, requestActionPayload);
    }

    private void processAerSubmissionToVerifierForAmends(AerApplicationRequestVerificationRequestTaskActionPayload actionPayload,
                                                         RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        final AerApplicationAmendsSubmitRequestTaskPayload taskPayload =
            (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        RequestActionPayload requestActionPayload;
        RequestActionType requestActionType;

        requestActionPayload =
            createApplicationAmendsSubmittedRequestActionPayload(requestTask, taskPayload);
        requestActionType = RequestActionType.AER_APPLICATION_AMENDS_SENT_TO_VERIFIER;

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the aer
        //this should be done before setting the aer object of the requestPayload with the corresponding from the task payload
        cleanUpDeprecatedReviewGroupDecisions((AerRequestPayload)request.getPayload(), taskPayload.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().keySet());

        // Validate AER
        AerContainer container = aerMapper.toAerContainer(taskPayload);
        aerValidatorService.validateAer(container);

        // Update verification sections completed
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        aerRequestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        // Submit AER
        submitAerForAmends(aerRequestPayload, requestTask, taskPayload.getAerSectionsCompleted(), pmrvUser,
            requestActionType,
            requestActionPayload);
    }

    private void cleanUpDeprecatedReviewGroupDecisions(AerRequestPayload aerRequestPayload, Set<MonitoringApproachType> newMonitoringApproaches) {
        Set<MonitoringApproachType> removedMonitoringApproaches =
            new HashSet<>(aerRequestPayload.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().keySet());
        removedMonitoringApproaches.removeAll(newMonitoringApproaches);

        if (!removedMonitoringApproaches.isEmpty()) {
            Set<AerReviewGroup> deprecatedReviewGroups = removedMonitoringApproaches.stream()
                .map(AerReviewGroup::getAerReviewGroupFromMonitoringApproach)
                .collect(Collectors.toSet());
            aerRequestPayload.getReviewGroupDecisions()
                .keySet()
                .removeAll(deprecatedReviewGroups);
        }
    }
}
