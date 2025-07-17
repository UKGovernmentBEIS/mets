package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
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

    public void sendToRegulator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerApplicationSubmitRequestTaskPayload taskPayload = (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport if verificationPerformed = false as in this case verification report is obsolete.
        // If it is mandatory it is handled by VerificationPerformedRequestTaskActionValidator
        if (!taskPayload.isVerificationPerformed()) {
            aerRequestPayload.setVerificationReport(null);
        }

        AerContainer container = aerMapper.toAerContainer(taskPayload, aerRequestPayload.getVerificationReport());
        aerValidatorService.validate(container, request.getAccountId());

        RequestActionPayload actionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, aerRequestPayload, RequestActionPayloadType.AER_APPLICATION_SUBMITTED_PAYLOAD);

        submitAer(aerRequestPayload, requestTask, appUser, RequestActionType.AER_APPLICATION_SUBMITTED, actionPayload, taskPayload.getAerSectionsCompleted());

        updateRequestMetadata(request, taskPayload, aerRequestPayload);
    }

    public void sendAmendsToRegulator(RequestTask requestTask, AerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload, AppUser appUser) {
        Request request = requestTask.getRequest();
        AerApplicationAmendsSubmitRequestTaskPayload taskPayload = (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();

        aerRequestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());
        if (!taskPayload.isVerificationPerformed()) {
            Map<AerReviewGroup, AerReviewDecision> verifierReviewGroupDecisions =
                    aerRequestPayload.getReviewGroupDecisions().entrySet().stream()
                            .filter(entry -> entry.getValue().getReviewDataType() == AerReviewDataType.VERIFICATION_REPORT_DATA)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            aerRequestPayload.getReviewGroupDecisions()
                    .keySet()
                    .removeAll(verifierReviewGroupDecisions.keySet());
            //UI cannot delete sectionsCompleted for verifier in case verificationReport is obsolete
            aerRequestPayload.getReviewSectionsCompleted()
                    .keySet()
                    .removeAll(AerReviewGroup.getVerificationDataReviewGroups(aerRequestPayload.getVerificationReport()).stream()
                            .map(Enum::name)
                            .collect(Collectors.toSet()));
            aerRequestPayload.setVerificationReport(null);
        }

        AerContainer container = aerMapper.toAerContainer(taskPayload, aerRequestPayload.getVerificationReport());
        aerValidatorService.validate(container, request.getAccountId());

        RequestActionPayload actionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, aerRequestPayload, RequestActionPayloadType.AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the aer
        //this should be done before setting the aer object of the requestPayload with the corresponding from the task payload
        removeReviewGroupDecisionsForRemovedMonitoringApproaches(aerRequestPayload, taskPayload.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().keySet());

        submitAer(aerRequestPayload, requestTask, appUser, RequestActionType.AER_APPLICATION_AMENDS_SUBMITTED, actionPayload, taskActionPayload.getAerSectionsCompleted());

        updateRequestMetadata(request, taskPayload, aerRequestPayload);
    }

    public void sendToVerifier(AerApplicationRequestVerificationRequestTaskActionPayload actionPayload,
                               RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerApplicationSubmitRequestTaskPayload taskPayload = (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        RequestActionPayload requestActionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, aerRequestPayload, RequestActionPayloadType.AER_APPLICATION_SUBMITTED_PAYLOAD);

        AerContainer container = aerMapper.toAerContainer(taskPayload);
        aerValidatorService.validateAer(container);

        aerRequestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitAer(aerRequestPayload, requestTask, appUser, RequestActionType.AER_APPLICATION_SENT_TO_VERIFIER, requestActionPayload, taskPayload.getAerSectionsCompleted());
    }

    public void sendAmendsToVerifier(AerApplicationRequestVerificationRequestTaskActionPayload actionPayload,
                                     RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerApplicationAmendsSubmitRequestTaskPayload taskPayload = (AerApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        aerRequestPayload.setReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());

        RequestActionPayload requestActionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, aerRequestPayload, RequestActionPayloadType.AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the aer
        //this should be done before setting the aer object of the requestPayload with the corresponding from the task payload
        removeReviewGroupDecisionsForRemovedMonitoringApproaches((AerRequestPayload)request.getPayload(), taskPayload.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().keySet());

        AerContainer container = aerMapper.toAerContainer(taskPayload);
        aerValidatorService.validateAer(container);

        aerRequestPayload.setVerificationSectionsCompleted(actionPayload.getVerificationSectionsCompleted());

        submitAer(aerRequestPayload, requestTask, appUser, RequestActionType.AER_APPLICATION_AMENDS_SENT_TO_VERIFIER, requestActionPayload, taskPayload.getAerSectionsCompleted());
    }

    public void sendToOperator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AerRequestPayload aerRequestPayload = (AerRequestPayload) request.getPayload();
        AerApplicationVerificationSubmitRequestTaskPayload taskPayload = (AerApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();

        aerValidatorService.validateVerificationReport(taskPayload.getVerificationReport(), taskPayload.getPermitOriginatedData());

        aerRequestPayload.setVerificationReport(taskPayload.getVerificationReport());
        aerRequestPayload.setVerificationPerformed(true);
        aerRequestPayload.getVerificationReport().setVerificationBodyId(request.getVerificationBodyId());
        aerRequestPayload.setVerificationSectionsCompleted(taskPayload.getVerificationSectionsCompleted());
        aerRequestPayload.setVerifiedAer(taskPayload.getAer());
        aerRequestPayload.setVerificationAttachments(taskPayload.getVerificationAttachments());

        // Add submitted action
        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId());
        AerApplicationVerificationSubmittedRequestActionPayload actionPayload = aerMapper.toAerApplicationVerificationSubmittedRequestActionPayload(taskPayload);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);

        requestService.addActionToRequest(request,
                actionPayload,
                RequestActionType.AER_APPLICATION_VERIFICATION_SUBMITTED,
                appUser.getUserId());
    }

    private void submitAer(AerRequestPayload aerRequestPayload,
                           RequestTask requestTask,
                           AppUser appUser,
                           RequestActionType requestActionType,
                           RequestActionPayload actionPayload,
                           Map<String, List<Boolean>> aerSectionsCompleted) {

        final AerApplicationSubmitRequestTaskPayload taskPayload =
                (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        aerRequestPayload.setAer(taskPayload.getAer());
        aerRequestPayload.setAerAttachments(taskPayload.getAerAttachments());
        aerRequestPayload.setAerSectionsCompleted(aerSectionsCompleted);
        aerRequestPayload.setVerificationPerformed(taskPayload.isVerificationPerformed());
        aerRequestPayload.setPermitOriginatedData(taskPayload.getPermitOriginatedData());
        aerRequestPayload.setMonitoringPlanVersions(taskPayload.getMonitoringPlanVersions());

        requestService.addActionToRequest(
                requestTask.getRequest(),
                actionPayload,
                requestActionType,
                appUser.getUserId());
    }

    private AerApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(RequestTask requestTask,
                                                                                                       AerApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                       AerRequestPayload aerRequestPayload,
                                                                                                       RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        AerApplicationSubmittedRequestActionPayload actionPayload = aerMapper.toAerApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);

        if (taskPayload.isVerificationPerformed()) {
            actionPayload.setVerificationReport(aerRequestPayload.getVerificationReport());
            actionPayload.setVerificationAttachments(aerRequestPayload.getVerificationAttachments());
        }
        return actionPayload;
    }

    private void updateRequestMetadata(Request request, AerApplicationSubmitRequestTaskPayload taskPayload,
                                       AerRequestPayload aerRequestPayload) {
        // Update Reportable Emissions
        final AerRequestMetadata metadata = (AerRequestMetadata) request.getMetadata();
        AerContainer aerContainer = aerMapper.toAerContainer(aerRequestPayload, taskPayload.getInstallationOperatorDetails(), metadata);
        AerSubmitParams params = AerSubmitParams.builder()
                .accountId(request.getAccountId())
                .aerContainer(aerContainer)
                .build();
        BigDecimal totalEmissions = aerService.updateReportableEmissions(params, false);

        // Save verifier decision and total emissions in request metadata
        Optional.ofNullable(aerContainer.getVerificationReport()).ifPresent(report ->
                metadata.setOverallAssessmentType(report.getVerificationData().getOverallAssessment().getType()));
        metadata.setEmissions(totalEmissions);
    }

    private void removeReviewGroupDecisionsForRemovedMonitoringApproaches(AerRequestPayload aerRequestPayload, Set<MonitoringApproachType> newMonitoringApproaches) {
        Set<MonitoringApproachType> removedMonitoringApproaches = new HashSet<>(aerRequestPayload.getAer().getMonitoringApproachEmissions().getMonitoringApproachEmissions().keySet());
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
