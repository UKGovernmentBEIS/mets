package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerTradingSchemeValidatorService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerUkEtsSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationUkEtsReportableEmissionsUpdateService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsSubmitApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.mapper.AviationAerUkEtsSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestAviationAerUkEtsSubmitService {
    private final AviationAerTradingSchemeValidatorService<AviationAerUkEtsContainer> aviationAerUkEtsValidatorService;
    private final RequestService requestService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;
    private final AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;
    private final AviationAerUkEtsSubmitMapper aviationAerUkEtsSubmitMapper;
    private final AviationUkEtsReportableEmissionsUpdateService aviationUkEtsReportableEmissionsUpdateService;

    public void sendToVerifier(AviationAerApplicationRequestVerificationRequestTaskActionPayload requestVerificationRequestTaskActionPayload,
                               RequestTask requestTask, AppUser appUser) {
        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        //validate aer
        AviationAerUkEtsContainer aerUkEtsContainer = aviationAerUkEtsSubmitMapper
            .toAviationAerUkEtsContainer(aviationAerSubmitRequestTaskPayload, EmissionTradingScheme.UK_ETS_AVIATION);
        aviationAerUkEtsValidatorService.validateAer(aerUkEtsContainer);

        //calculate submitted emissions
        AviationAerUkEtsSubmittedEmissions submittedEmissions = calculateSubmittedEmissions(aerUkEtsContainer);

        //update request payload
        Request request = requestTask.getRequest();
        AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        updateAviationAerUkEtsRequestPayload(aviationAerUkEtsRequestPayload, aviationAerSubmitRequestTaskPayload, submittedEmissions);
        aviationAerUkEtsRequestPayload.setVerificationSectionsCompleted(requestVerificationRequestTaskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(
            aviationAerSubmitRequestTaskPayload,
            submittedEmissions,
            request,
            appUser,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER
        );
    }

    public void sendToRegulator(AviationAerUkEtsSubmitApplicationRequestTaskActionPayload requestTaskActionPayload, RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        AviationAerUkEtsRequestPayload requestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();

        AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload =
            (AviationAerUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        //Delete verificationReport if verificationPerformed = false as in this case verification report is obsolete.
        if (!aviationAerSubmitRequestTaskPayload.isVerificationPerformed()) {
        	requestPayload.setVerificationReport(null);
        }

        //validate
        AviationAerUkEtsContainer aerUkEtsContainer =
            aviationAerUkEtsSubmitMapper.toAviationAerUkEtsContainer(
                aviationAerSubmitRequestTaskPayload, requestPayload.getVerificationReport(), EmissionTradingScheme.UK_ETS_AVIATION);
        aviationAerUkEtsValidatorService.validate(aerUkEtsContainer);

        //calculate submitted emissions
        AviationAerUkEtsSubmittedEmissions submittedEmissions = calculateSubmittedEmissions(aerUkEtsContainer);

        //update request payload
        updateAviationAerUkEtsRequestPayload(requestPayload, aviationAerSubmitRequestTaskPayload, submittedEmissions);
        requestPayload.setVerificationSectionsCompleted(requestTaskActionPayload.getVerificationSectionsCompleted());

        //add request action
        addApplicationSubmittedRequestAction(
            aviationAerSubmitRequestTaskPayload,
            submittedEmissions,
            request,
            appUser,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED
        );

        //update request metadata with reportable emissions
        if(Boolean.TRUE.equals(aerUkEtsContainer.getReportingRequired())) {
            updateReportableEmissionsAndRequestMetadata(request, aerUkEtsContainer);
        }
    }

    private AviationAerUkEtsSubmittedEmissions calculateSubmittedEmissions(AviationAerUkEtsContainer aerUkEtsContainer) {
        return Boolean.TRUE.equals(aerUkEtsContainer.getReportingRequired())
            ? aviationAerUkEtsEmissionsCalculationService.calculateSubmittedEmissions(aerUkEtsContainer)
            : null;
    }

    private void updateAviationAerUkEtsRequestPayload(AviationAerUkEtsRequestPayload aviationAerUkEtsRequestPayload,
                                                      AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload,
                                                      AviationAerUkEtsSubmittedEmissions submittedEmissions) {
        aviationAerUkEtsRequestPayload.setReportingRequired(aviationAerSubmitRequestTaskPayload.getReportingRequired());
        aviationAerUkEtsRequestPayload.setReportingObligationDetails(aviationAerSubmitRequestTaskPayload.getReportingObligationDetails());
        aviationAerUkEtsRequestPayload.setAer(aviationAerSubmitRequestTaskPayload.getAer());
        aviationAerUkEtsRequestPayload.setAerAttachments(aviationAerSubmitRequestTaskPayload.getAerAttachments());
        aviationAerUkEtsRequestPayload.setAerSectionsCompleted(aviationAerSubmitRequestTaskPayload.getAerSectionsCompleted());
        aviationAerUkEtsRequestPayload.setVerificationPerformed(aviationAerSubmitRequestTaskPayload.isVerificationPerformed());
        aviationAerUkEtsRequestPayload.setAerMonitoringPlanVersions(aviationAerSubmitRequestTaskPayload.getAerMonitoringPlanVersions());
        aviationAerUkEtsRequestPayload.setEmpOriginatedData(aviationAerSubmitRequestTaskPayload.getEmpOriginatedData());
        aviationAerUkEtsRequestPayload.setSubmittedEmissions(submittedEmissions);
    }

    private void addApplicationSubmittedRequestAction(AviationAerUkEtsApplicationSubmitRequestTaskPayload aviationAerSubmitRequestTaskPayload,
                                                      AviationAerUkEtsSubmittedEmissions submittedEmissions,Request request, AppUser appUser,
                                                      RequestActionType requestActionType) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerUkEtsApplicationSubmittedRequestActionPayload aviationAerUkEtsApplicationSubmittedPayload =
            aviationAerUkEtsSubmitMapper.toAviationAerUkEtsApplicationSubmittedRequestActionPayload(
                aviationAerSubmitRequestTaskPayload, accountInfo, submittedEmissions, RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD);
        if (aviationAerSubmitRequestTaskPayload.isVerificationPerformed()) {
            aviationAerUkEtsApplicationSubmittedPayload.setVerificationReport(((AviationAerUkEtsRequestPayload) request.getPayload()).getVerificationReport());
        }
        requestService.addActionToRequest(request, aviationAerUkEtsApplicationSubmittedPayload, requestActionType, appUser.getUserId());
    }

    private void updateReportableEmissionsAndRequestMetadata(Request request, AviationAerUkEtsContainer aerUkEtsContainer) {
        AviationAerTotalReportableEmissions totalReportableEmissions =
            aviationReportableEmissionsService.updateReportableEmissions(aerUkEtsContainer, request.getAccountId(), false);

        AviationAerRequestMetadata metadata = (AviationAerRequestMetadata) request.getMetadata();
        metadata.setEmissions(totalReportableEmissions.getReportableEmissions());

        Optional.ofNullable(aerUkEtsContainer.getVerificationReport()).ifPresent(report ->
            metadata.setOverallAssessmentType(report.getVerificationData().getOverallDecision().getType()));
    }
}
