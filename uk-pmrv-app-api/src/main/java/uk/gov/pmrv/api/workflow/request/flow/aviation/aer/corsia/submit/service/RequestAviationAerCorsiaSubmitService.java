package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.mapper.AviationAerCorsiaSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestAviationAerCorsiaSubmitService {

    private final AviationAerTradingSchemeValidatorService<AviationAerCorsiaContainer> aviationAerCorsiaValidatorService;
    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaSubmittedEmissionsCalculationService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;
    private final AviationAerCorsiaSubmitMapper aviationAerCorsiaSubmitMapper;

    public void sendToVerifier(AviationAerApplicationRequestVerificationRequestTaskActionPayload requestVerificationRequestTaskActionPayload,
                               RequestTask requestTask, PmrvUser pmrvUser) {
        AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
                (AviationAerCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate aer
        AviationAerCorsiaContainer aerCorsiaContainer = aviationAerCorsiaSubmitMapper
                .toAviationAerCorsiaContainer(aviationAerSubmitRequestTaskPayload, EmissionTradingScheme.CORSIA);
        aviationAerCorsiaValidatorService.validateAer(aerCorsiaContainer);

        // Update request payload
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        updateAviationAerCorsiaRequestPayload(aviationAerCorsiaRequestPayload, aviationAerSubmitRequestTaskPayload, aerCorsiaContainer);
        aviationAerCorsiaRequestPayload.setVerificationSectionsCompleted(requestVerificationRequestTaskActionPayload.getVerificationSectionsCompleted());

        // Add request action
        addApplicationSubmittedRequestAction(aviationAerSubmitRequestTaskPayload, request, pmrvUser, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER);
    }

    public void sendToRegulator(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();

        AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
                (AviationAerCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Delete verificationReport if verificationPerformed = false as in this case verification report is obsolete.
        if (!aviationAerSubmitRequestTaskPayload.isVerificationPerformed()) {
            aviationAerCorsiaRequestPayload.setVerificationReport(null);
        }

        AviationAerCorsiaContainer aerCorsiaContainer =
                aviationAerCorsiaSubmitMapper.toAviationAerCorsiaContainer(
                        aviationAerSubmitRequestTaskPayload, aviationAerCorsiaRequestPayload.getVerificationReport(), EmissionTradingScheme.CORSIA);

        // Set total emissions in container
        if (Boolean.TRUE.equals(aerCorsiaContainer.getReportingRequired())) {
            updateTotalEmissions(aerCorsiaContainer);
        }

        // Validate
        aviationAerCorsiaValidatorService.validate(aerCorsiaContainer);

        // Update request payload
        updateAviationAerCorsiaRequestPayload(aviationAerCorsiaRequestPayload, aviationAerSubmitRequestTaskPayload, aerCorsiaContainer);

        // Add request action
        addApplicationSubmittedRequestAction(aviationAerSubmitRequestTaskPayload, request, pmrvUser, RequestActionType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED);

        // Update request metadata with reportable emissions
        if (Boolean.TRUE.equals(aerCorsiaContainer.getReportingRequired())) {
            updateReportableEmissionsAndRequestMetadata(request, aerCorsiaContainer);
        }
    }

    private void updateAviationAerCorsiaRequestPayload(AviationAerCorsiaRequestPayload aviationAerCorsiaRequestPayload,
                                                       AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload,
                                                       AviationAerCorsiaContainer aerCorsiaContainer) {

        aviationAerCorsiaRequestPayload.setReportingRequired(aviationAerSubmitRequestTaskPayload.getReportingRequired());
        aviationAerCorsiaRequestPayload.setReportingObligationDetails(aviationAerSubmitRequestTaskPayload.getReportingObligationDetails());
        aviationAerCorsiaRequestPayload.setAer(aviationAerSubmitRequestTaskPayload.getAer());
        aviationAerCorsiaRequestPayload.setAerAttachments(aviationAerSubmitRequestTaskPayload.getAerAttachments());
        aviationAerCorsiaRequestPayload.setAerSectionsCompleted(aviationAerSubmitRequestTaskPayload.getAerSectionsCompleted());
        aviationAerCorsiaRequestPayload.setVerificationPerformed(aviationAerSubmitRequestTaskPayload.isVerificationPerformed());
        aviationAerCorsiaRequestPayload.setAerMonitoringPlanVersions(aviationAerSubmitRequestTaskPayload.getAerMonitoringPlanVersions());
        aviationAerCorsiaRequestPayload.setEmpOriginatedData(aviationAerSubmitRequestTaskPayload.getEmpOriginatedData());

        Optional.ofNullable(aerCorsiaContainer.getSubmittedEmissions()).ifPresent(submittedEmissions -> {
            AviationAerCorsiaSubmittedEmissions corsiaSubmittedEmissions = (AviationAerCorsiaSubmittedEmissions) submittedEmissions;
            aviationAerCorsiaRequestPayload.setTotalEmissionsProvided(corsiaSubmittedEmissions.getTotalEmissions().getAllFlightsEmissions());
            aviationAerCorsiaRequestPayload.setTotalOffsetEmissionsProvided(corsiaSubmittedEmissions.getTotalEmissions().getOffsetFlightsEmissions());
        });
    }

    private void addApplicationSubmittedRequestAction(AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload,
                                                      Request request, PmrvUser pmrvUser, RequestActionType requestActionType) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerCorsiaApplicationSubmittedRequestActionPayload aviationAerCorsiaApplicationSubmittedPayload =
                aviationAerCorsiaSubmitMapper.toAviationAerCorsiaApplicationSubmittedRequestActionPayload(
                        aviationAerSubmitRequestTaskPayload, accountInfo, RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD);

        requestService.addActionToRequest(request, aviationAerCorsiaApplicationSubmittedPayload, requestActionType, pmrvUser.getUserId());
    }

    private void updateTotalEmissions(AviationAerCorsiaContainer aerCorsiaContainer) {
        AviationAerCorsiaSubmittedEmissions submittedEmissions = aviationAerCorsiaSubmittedEmissionsCalculationService
                .calculateSubmittedEmissions(aerCorsiaContainer);
        aerCorsiaContainer.setSubmittedEmissions(submittedEmissions);
    }

    private void updateReportableEmissionsAndRequestMetadata(Request request, AviationAerCorsiaContainer aerCorsiaContainer) {
        // Save reporting emissions in DB
        AviationAerTotalReportableEmissions totalReportableEmissions =
            aviationReportableEmissionsService.updateReportableEmissions(aerCorsiaContainer, request.getAccountId());

        // Update metadata
        AviationAerCorsiaTotalReportableEmissions totalEmissions = (AviationAerCorsiaTotalReportableEmissions) totalReportableEmissions;
        AviationAerCorsiaRequestMetadata metadata = (AviationAerCorsiaRequestMetadata) request.getMetadata();

        metadata.setEmissions(totalEmissions.getReportableEmissions());
        metadata.setTotalEmissionsOffsettingFlights(totalEmissions.getReportableOffsetEmissions());
        metadata.setTotalEmissionsClaimedReductions(totalEmissions.getReportableReductionClaimEmissions());

        Optional.ofNullable(aerCorsiaContainer.getVerificationReport()).ifPresent(report ->
            metadata.setOverallAssessmentType(report.getVerificationData().getOverallDecision().getType()));
    }
}
